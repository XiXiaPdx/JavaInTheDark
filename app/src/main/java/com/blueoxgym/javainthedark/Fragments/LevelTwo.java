package com.blueoxgym.javainthedark.Fragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blueoxgym.javainthedark.MainActivity;
import com.blueoxgym.javainthedark.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class LevelTwo extends Fragment implements View.OnClickListener {
    @BindView(R.id.txt_output)
    TextView txtOutput;
    @BindView(R.id.btn_mic)
    ImageButton btnMicrophone;
    @BindView(R.id.lyricTextView)
    TextView lyricText;
    @BindView(R.id.levelTextView)
    TextView levelText;
    @BindView(R.id.progressBarMic)
    ProgressBar micLevels;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String lyric;
    private String verseNoPunc;
    private int currentLevel = 1;
    private String[] referenceWords;
    private ArrayList displayWords;
    private ArrayList previousDisplayWords;
    private final String TAG = "Voice Recognition";

    public LevelTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_level_two, container, false);
        ButterKnife.bind(this, view);
        btnMicrophone.setOnClickListener(this);
        lyric = getArguments().getString("oneVerse", "");
        setVerseNoPunc(lyric);
        lyricText.setText(lyric);
        levelText.setText("LEVEL " + currentLevel);
        startLevel();
        return view;
    }

    public static LevelTwo newInstance(String verse) {
        LevelTwo levelTwoFragment = new LevelTwo();
        Bundle args = new Bundle();
        args.putString("oneVerse", verse);
        levelTwoFragment.setArguments(args);
        return levelTwoFragment;
    }

    @Override
    public void onClick(View v) {
        if (v == btnMicrophone) {
            startSpeechToText();
        }

    }

    public void checkForMatch(String speech) {
            checkEachWord(speech);
    }

    public Boolean isInstaMatch(String speech) {
        if (speech.toLowerCase().equals(verseNoPunc)) {
            txtOutput.setText("You got it! ");
            return true;
        } else {
            return false;
        }
    }

    public void checkEachWord(String speech) {
        String[] speechWords = speech.split(" ");
        txtOutput.setText("Close! Please Try Again");
        //reset displayWord
        displayWords = new ArrayList<String>();
        //compare each word in speech array with reference array
        for (int i = 0; i < referenceWords.length; i++) {
            //speaker can say too few words. Check to prevent indexOutofBounds
            if (i < speechWords.length) {
                // need to remove punctuation from this word
                String currentWordNoPunc = removeWordPunc(referenceWords[i]);
                Log.d("CURRENT WORD", currentWordNoPunc);
                String speechWord = speechWords[i].toLowerCase();
                if (speechWord.equals(currentWordNoPunc)
                        ) {
                    //if word is match, pull original from reference WHICH HAS PUNC and add to display
                    displayWords.add(referenceWords[i]);
                } else {
                    // word doesn't match, reveal more hints.
                    revealMoreLetters(i);
                }
            } else {
                //speech too short as compared to reference
//                revealMoreLetters(i);
                setHintWords(referenceWords[i]);
            }
        }
        setLyricTextView();
    }

    public void revealMoreLetters(int i) {
        // We know speech did not equal reference
        // use previousDisplayWords to know where to begin inserting new letter
        String previousWord = previousDisplayWords.get(i).toString();
        if (alreadySolved(previousWord)) {
            setHintWords(previousWord);
            return;
        }
        StringBuilder tempWord = new StringBuilder(previousWord);
        int dashCount = 0;
        for (int k = 0; k < previousWord.length(); k++) {
            if (String.valueOf(previousWord.charAt(k)).equals("-")) {
                dashCount++;
            }
        }
        switch (currentLevel) {
            case 1:
                if (dashCount > 1) {
                    for (int j = 0; j < previousWord.length(); j++) {
                        if (String.valueOf(previousWord.charAt(j)).equals("-")) {
                            tempWord.setCharAt(j, referenceWords[i].charAt(j));
                            displayWords.add(tempWord);
                            break;
                        }
                    }
                } else {
                    displayWords.add(previousWord);
                }
                break;
            case 2:
            case 4:
                displayWords.add(previousWord);
                break;
            case 3:
                if (dashCount > 1) {
                    Random ran = new Random();
                    int randomIndex = 0;
                    Character randomLetter;
                    do {
                        randomIndex = ran.nextInt(referenceWords[i].length());
                        randomLetter = referenceWords[i].charAt(randomIndex);
                    }
                    while (randomIndex == 0 || ifEndInPunc(randomLetter) || isAtoZ(tempWord.charAt(randomIndex)));
                    tempWord.setCharAt(randomIndex, randomLetter);
                    displayWords.add(tempWord);
                } else {
                    displayWords.add(previousWord);
                }
                break;

        }
    }

    public void startLevel() {
        displayWords = new ArrayList<String>();
        //make Display array of lyric words
        referenceWords = lyric.split(" ");
            for (int i = 0; i < referenceWords.length; i++) {
                String tempWord = referenceWords[i];
                setHintWords(tempWord);
            }
        setLyricTextView();
    }


    public void setHintWords(String word) {
        String tempWord = removeWordPunc(word);
        String newDisplayWord = "";
        switch (currentLevel) {
            case 1:
            case 3:
                if (isOneLetterWord(tempWord)) {
                    break;
                } else {
                    //loop over it and create new word character by character
                    for (int j = 0; j < tempWord.length(); j++) {
                        // display first letter of word
                        if (j == 0 && currentLevel == 1) {
                            newDisplayWord = newDisplayWord + tempWord.charAt(j);
                        } else {
                            // add "-"
                            if (isAtoZ(tempWord.charAt(j))) {
                                newDisplayWord = newDisplayWord + "-";
                            } else {
                                // keep punctuation
                                newDisplayWord = newDisplayWord + tempWord.charAt(j);
                            }
                        }
                    }
                    displayWords.add(newDisplayWord);
                }
                break;
            case 2:
            case 4:
                for (int j = 0; j < tempWord.length(); j++) {
                    // display first letter of word
                    if (j == 0) {
                        switch (currentLevel) {
                            case 2:
                                newDisplayWord = newDisplayWord + tempWord.charAt(j);
                                break;
                            case 4:
                                newDisplayWord = newDisplayWord + "-";
                                break;
                        }
                    } else if (j == (tempWord.length() - 1) && ifEndInPunc(tempWord.charAt(j))) {
                        newDisplayWord = newDisplayWord + tempWord.charAt(j);
                    }
                }
                displayWords.add(newDisplayWord);
                break;
        }
    }


    public Boolean alreadySolved(String word) {
        if (word.contains("-")) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean isAtoZ(char letter) {
        if (String.valueOf(letter).matches("[a-zA-Z]")) {
            return true;
        }
        return false;
    }

    public Boolean isOneLetterWord(String tempWord) {
        if (tempWord.length() == 1) {
            displayWords.add("-");
            return true;
        } else if (tempWord.length() == 2 && ifEndInPunc(tempWord.charAt(1))) {
            // corner case   "I,"
            displayWords.add("-" + tempWord.charAt(1));
            return true;
        } else {
            return false;
        }
    }

    public void setVerseNoPunc(String lyric) {
        String[] words = lyric.replaceAll("[^a-zA-Z' ]", "").toLowerCase().split("\\s+");
        verseNoPunc = TextUtils.join(" ", words);
    }

    public String removeWordPunc(String word) {
        return word.replaceAll("[^a-zA-Z' ]", "").toLowerCase();
    }

    public Boolean ifEndInPunc(Character letter) {
        Pattern p = Pattern.compile("[,.?!:']");
        Matcher m = p.matcher(String.valueOf(letter));
        if (m.find()) {
            return true;
        }
        return false;
    }

    public void setLyricTextView() {
        previousDisplayWords = displayWords;
        lyricText.setText(TextUtils.join(" ", displayWords));
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
            Log.d(TAG, "error " + error);
        }

        @Override
        public void onResults(Bundle results) {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = data.get(0).toLowerCase();
            if (isInstaMatch(text)) {
                currentLevel += 1;
                startLevel();
            } else {
                checkForMatch(text);
            }
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
