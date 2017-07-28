package com.blueoxgym.javainthedark.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blueoxgym.javainthedark.MainActivity;
import com.blueoxgym.javainthedark.MusicMatch.EachTrack;
import com.blueoxgym.javainthedark.MusicMatch.LyricsModel;
import com.blueoxgym.javainthedark.MusicMatch.MusicMatchClient;
import com.blueoxgym.javainthedark.MusicMatch.ServiceGenerator;
import com.blueoxgym.javainthedark.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.blueoxgym.javainthedark.Constants.ARTIST_NAME;
import static com.blueoxgym.javainthedark.Constants.MUSIC_MATCH_KEY;
import static com.blueoxgym.javainthedark.Constants.SONG_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class MicFragment extends Fragment implements  View.OnClickListener {
    @BindView(R.id.progressBarMic)
    ProgressBar micLevels;
    @BindView(R.id.btn_mic)
    ImageButton btnMicrophone;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    public final static String TAG = "In speech mode";
    public FragmentManager fragmentManager;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String trackName;
    private String artistName;
    private CallMainLoadVerseFragment loadVerseFragment;
    private CheckSpeech checkSpeechOnVerse;
    private ProgressDialog speechLoading;
    private Boolean isMicOn;


    public MicFragment() {
        // Required empty public constructor
    }

    public static MicFragment newInstance (){
        MicFragment micFragment = new MicFragment();
        return micFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mic, container, false);
        ButterKnife.bind(this, view);
        this.loadVerseFragment = (CallMainLoadVerseFragment) getActivity();
        this.checkSpeechOnVerse = (CheckSpeech) getActivity();
        btnMicrophone.setOnClickListener(this);
        fragmentManager = getFragmentManager();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mSharedPreferences.edit();
        speechLoadingDialog();
        isMicOn = false;
        return view;
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

    public void speechLoadingDialog(){
        speechLoading = new ProgressDialog(getContext());
        speechLoading.setTitle(getString(R.string.speech_loading));
        speechLoading.setCancelable(false);
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
            speech.destroy();
            Log.d(TAG, "error " + error);
        }


        @Override
        public void onResults(Bundle results) {
            isMicOn = false;
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = data.get(0).toLowerCase().replace("by","");
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);
            if (currentFragment.toString().contains("LyricSearch")){
                searchForSong(text);
            } else if (currentFragment.toString().contains("VersesList")){
                checkSpeechOnVerse.checkingSpeech(text);
            }
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
        isMicOn = true;
        speechLoading.show();
        btnMicrophone.setBackgroundResource(R.drawable.circle_green);
        speech=SpeechRecognizer.createSpeechRecognizer(getContext());
        speech.setRecognitionListener(new listener());
        recognizerIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "HEY HEY");
        speech.startListening(recognizerIntent);
    }

    public void getLyricsCall(int songId){
        MusicMatchClient client = ServiceGenerator.createService(MusicMatchClient.class);
        Observable<LyricsModel> call = client.songLyrics(Integer.toString(songId), MUSIC_MATCH_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        //observer
        Observer<LyricsModel> observer = new Observer<LyricsModel>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe" + Thread.currentThread().getName());
            }

            @Override
            public void onNext(LyricsModel value) {
                String lyrics = value.getMessage().getBody().getLyrics().getLyrics_body();
                loadVerseFragment.loadVerseFragmentCall(lyrics);
                Log.e(TAG, "onNext: " +  value.getMessage().getBody().getLyrics().getLyrics_body()+ Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: All Done!" + Thread.currentThread().getName());
            }

        };
        call.subscribe(observer);
    }


    public void searchForSong(String searchTerms){
        MusicMatchClient client = ServiceGenerator.createService(MusicMatchClient.class);
        Observable<LyricsModel> call = client.songSearch(searchTerms, MUSIC_MATCH_KEY, "true", "5")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer<LyricsModel> observer = new Observer<LyricsModel>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe" + Thread.currentThread().getName());
            }

            @Override
            public void onNext(LyricsModel value) {
                Log.e(TAG, "onNext: "  + Thread.currentThread().getName());
                List<EachTrack> trackList = value.getMessage().getBody().getTrack_list();
                try {
                    getLyricsCall(trackList.get(0).getTrack().getTrack_id());
                    trackName = trackList.get(0).getTrack().getTrack_name();
                    artistName = trackList.get(0).getTrack().getArtist_name();
                    addSongAndArtistShared(artistName, trackName);

                } catch (IndexOutOfBoundsException e){
                    searchError();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: All Done!" + Thread.currentThread().getName());
            }

        };
        call.subscribe(observer);
    }

    public void addSongAndArtistShared(String artist, String song){
        mEditor.putString(ARTIST_NAME, artist).apply();
        mEditor.putString(SONG_NAME, song).apply();
    }

    public void searchError() {
        Toast.makeText((MainActivity)getActivity(), "Sorry, we don't have lyrics for that song.", Toast.LENGTH_SHORT).show();
    }

    public interface CallMainLoadVerseFragment {
        void loadVerseFragmentCall(String lyrics);
    }

    public interface CheckSpeech {
        void checkingSpeech (String text);
    }
}
