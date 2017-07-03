package com.blueoxgym.javainthedark.Fragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blueoxgym.javainthedark.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class LevelTwo extends Fragment implements View.OnClickListener {
    @BindView(R.id.txt_output)TextView txtOutput;
    @BindView(R.id.btn_mic) ImageButton btnMicrophone;
    @BindView(R.id.lyricTextView) TextView lyricText;
    @BindView(R.id.levelTextView) TextView levelText;
    private final int SPEECH_RECOGNITION_CODE = 1;
    private final String lyric = "Jessica's, This Way.";
    private String verseNoPunc;
    private int currentLevel = 1;
//    private String[] referenceWords;
    private ArrayList displayWords;

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
        setVerseNoPunc(lyric);
        lyricText.setText(lyric);
        levelText.setText("LEVEL "+ currentLevel);
        displayWords = new ArrayList<String>();
        startLevelTwo();

        return view;
    }

    public static LevelTwo newInstance(){
        LevelTwo levelTwoFragment = new LevelTwo();
        return levelTwoFragment;
    }

    @Override
    public void onClick(View v) {
        if (v == btnMicrophone){
            startSpeechToText();
        }

    }

    public void startSpeechToText(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0).toLowerCase();
                    checkForMatch(text);

                }
                break;
            }
        }
    }

    public void checkForMatch(String speech){
        if (currentLevel == 1){
           if(isInstaMatch(speech)){
               //advance to Level 2
               startLevelTwo();
           } else {
               txtOutput.setText("Please try again..." + speech);
           }
        } else if (currentLevel == 2){
            if (isInstaMatch(speech)){
                lyricText.setText(lyric);
            } else {
                String[] speechWords = speech.split(" ");
                Log.d("Speech words", TextUtils.join(" ", speechWords));
            }
        }
    }

    public Boolean isInstaMatch(String speech){
        if(speech.equals(verseNoPunc)) {
            txtOutput.setText(speech);
            return true;
        } else {
            return false;
        }
    }

    public void startLevelTwo() {
        currentLevel = 2;
        levelText.setText("LEVEL " + 2);
        showFirstLetter();

    }


    public void showFirstLetter(){
        String[] referenceWords = lyric.split(" ");
        //make Display array of words
        for ( int i=0; i < referenceWords.length; i++){
            //current word in the loop
            String tempWord = referenceWords[i];
            //if word is length 1, then only "-"
            if (tempWord.length() == 1){
                displayWords.add("-");
                // corner case   "I,"  will create issues
            } else {
                //loop over it and create new word character by character
                String newDisplayWord="";
                for (int j=0; j < tempWord.length(); j++){
                    // display first letter of word
                    if (j==0){
                        newDisplayWord = newDisplayWord + tempWord.charAt(j);
                    } else {
                        // add "-"
                        if (String.valueOf(tempWord.charAt(j)).matches("[a-zA-Z]")){
                            newDisplayWord = newDisplayWord +"-";
                        } else {
                            // keep punctuation
                            newDisplayWord = newDisplayWord + tempWord.charAt(j);
                        }
                    }
                }
                displayWords.add(newDisplayWord);
            }
        }
        lyricText.setText(TextUtils.join(" ", displayWords));
    }

    public void setVerseNoPunc(String lyric){
        String [] words = lyric.replaceAll("[^a-zA-Z' ]", "").toLowerCase().split("\\s+");
        verseNoPunc = TextUtils.join(" ", words);
    }


}
