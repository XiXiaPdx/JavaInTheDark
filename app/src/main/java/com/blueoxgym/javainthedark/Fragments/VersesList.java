package com.blueoxgym.javainthedark.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        return view;
    }

    public static VersesList newInstance(String lyrics){
        VersesList versesListFragment = new VersesList();
        Bundle args = new Bundle();
        args.putString("lyrics", lyrics);
        versesListFragment.setArguments(args);
        return versesListFragment;

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
               finalModVerseList.add(addThisVerse);
//               finalModVerseList.add(verseList.get(i).toString());
           }
        }
    }

    public void displayArtistAndSongName(){
        String song = '"'+mSharedPreferences.getString(SONG_NAME, null)+'"';
        String artist = "by "+mSharedPreferences.getString(ARTIST_NAME, null);
        songName.setText(song);
        artistName.setText(artist);
    }

    public void setVersesIntoRecyclerView(){
        verseAdapter = new VerseAdapter(finalModVerseList, (MainActivity)getActivity(), versesRecycleView);
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

    public void setVersesScrollListener(){
        versesRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == 0) {
                    verseAdapter.resetVerse();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnMicrophone) {
            startSpeechToText();
        }

    }


    class listener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
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
            speech.stopListening();
        }

        @Override
        public void onError(int error) {
            btnMicrophone.setBackgroundResource(R.drawable.circle_transparent);
            speech.stopListening();
            Log.d(TAG, "error " + error);
        }


        @Override
        public void onResults(Bundle results) {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = data.get(0).toLowerCase().replace("by","");
            verseAdapter.checkForMatch(text);

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");

        }

        @Override
        public void onEvent(int eventType, Bundle what) {
            Log.d(TAG, "onEvent " + eventType);
        }

    }
    public void startSpeechToText(){
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
