package com.blueoxgym.javainthedark.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blueoxgym.javainthedark.MainActivity;
import com.blueoxgym.javainthedark.R;
import com.blueoxgym.javainthedark.adapter.VerseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.blueoxgym.javainthedark.Constants.ARTIST_NAME;
import static com.blueoxgym.javainthedark.Constants.SONG_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class VersesList extends Fragment implements  View.OnClickListener {
    @BindView(R.id.progressBarMic)
    ProgressBar micLevels;
    @BindView(R.id.btn_mic)
    ImageButton btnMicrophone;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    public final static String TAG = "In speech mode";
    @BindView(R.id.versesRecycleView) RecyclerView versesRecycleView;
    @BindView(R.id.songNameTextView) TextView songName;
    @BindView(R.id.artistTextView)TextView artistName;
    private SharedPreferences mSharedPreferences;
    LinearLayoutManager llm;
    List verseList;
    List finalModVerseList;
    private VerseAdapter verseAdapter;
    // temporary
    private SharedPreferences.Editor editor;
    private ProgressDialog speechLoading;
    private Boolean isMicOn;
    private int currentVisible;

    public VersesList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_verses_list, container, false);
        ButterKnife.bind(this, view);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor=mSharedPreferences.edit();
        btnMicrophone.setOnClickListener(this);
        displayArtistAndSongName();
        lyricsToVerseList();
        setVersesIntoRecyclerView();
        setVersesScrollListener();
        //temp
        storeAllVerseLevels();
        speechLoadingDialog();
        isMicOn = false;


        return view;
    }


    public static VersesList newInstance(String lyrics){
        VersesList versesListFragment = new VersesList();
        Bundle args = new Bundle();
        args.putString("lyrics", lyrics);
        versesListFragment.setArguments(args);
        return versesListFragment;
    }

    public void speechLoadingDialog(){
        speechLoading = new ProgressDialog(getContext());
        speechLoading.setTitle(getString(R.string.speech_loading));
        speechLoading.setCancelable(false);
    }

    public void lyricsToVerseList(){
        String lyrics = getArguments().getString("lyrics", "");
        verseList = new ArrayList<String>();
        finalModVerseList = new ArrayList<String>();
        verseList= Arrays.asList(lyrics.split("\n"));
        int endOfFinalList=verseList.indexOf("...");
        for (int i = 0; i < endOfFinalList; i++) {
           if(!verseList.get(i).toString().equals("")){
               String addThisVerse = verseList.get(i).toString();
               //check on length of verse, if too short add next, check again
               int numberOfWords = addThisVerse.split(" ").length;
               while (numberOfWords < 10 && i < endOfFinalList) {
                   i++;
                   addThisVerse += " " + verseList.get(i).toString();
                   numberOfWords = addThisVerse.split(" ").length;
               }
               finalModVerseList.add(scrubbed(addThisVerse));
           }
        }
    }

    public String scrubbed (String verseToScrub){
        return verseToScrub;
    }

    public void displayArtistAndSongName(){
        String song = '"'+mSharedPreferences.getString(SONG_NAME, null)+'"';
        String artist = "by "+mSharedPreferences.getString(ARTIST_NAME, null);
        songName.setText(song);
        artistName.setText(artist);
    }

    public void setVersesIntoRecyclerView(){
        verseAdapter = new VerseAdapter(finalModVerseList, this.getContext(), versesRecycleView, micLevels, btnMicrophone, VersesList.this);
        versesRecycleView.setAdapter(verseAdapter);
        llm = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        versesRecycleView.setLayoutManager(llm);
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(versesRecycleView);
    }

    private void storeLevel(int indexNumber) {
        editor.putInt(String.valueOf(indexNumber), 1).apply();
    }

    private void storeAllVerseLevels(){
        for (int i=0; i< finalModVerseList.size();i++){
            storeLevel(i);
        }
        for (int j=0; j< finalModVerseList.size();j++){
            String level = String.valueOf(mSharedPreferences.getInt(String.valueOf(j), -1));
            Log.d("In Shared Preferences  ", "Verse "+j+" Level "+level);
        }
    }

    public void setVersesScrollListener() {
        versesRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    currentVisible = llm.findLastCompletelyVisibleItemPosition();
                    verseAdapter.setStars(currentVisible);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                verseAdapter.resetVerse();
            }
        });
    }

    public void scrollToNext(){
        RecyclerView.State state = new RecyclerView.State();
        llm.smoothScrollToPosition(versesRecycleView, state  ,currentVisible+1);
    }

    @Override
    public void onClick(View v) {
        if (v == btnMicrophone) {
            if(!isMicOn){
            startSpeechToText();
        } else {
                speech.stopListening();
                isMicOn = false;
            }
        }
    }


    class listener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
            speechLoading.dismiss();
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
            micLevels.setMax(15);
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, String.valueOf(rmsdB));
            micLevels.setProgress((int) rmsdB);
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");

        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
            micLevels.setProgress(6);
            btnMicrophone.setBackgroundResource(R.drawable.circle_transparent);
        }

        @Override
        public void onError(int error) {
            speechLoading.dismiss();
            btnMicrophone.setBackgroundResource(R.drawable.circle_transparent);
            Log.d(TAG, "error " + error);
            speech.destroy();
        }


        @Override
        public void onResults(Bundle results) {
            isMicOn = false;
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = data.get(0).toLowerCase();
            verseAdapter.checkForMatch(text);
            speech.destroy();

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
            speech.destroy();

        }

        @Override
        public void onEvent(int eventType, Bundle what) {
            Log.d(TAG, "onEvent " + eventType);
        }

    }
    public void startSpeechToText(){
        speechLoading.show();
        isMicOn = true;
        btnMicrophone.setBackgroundResource(R.drawable.circle_green);
        speech=SpeechRecognizer.createSpeechRecognizer(getContext());
        speech.setRecognitionListener(new listener());
        recognizerIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speech.startListening(recognizerIntent);
    }
}
