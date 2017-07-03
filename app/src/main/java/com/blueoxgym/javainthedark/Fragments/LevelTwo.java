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
    private final int SPEECH_RECOGNITION_CODE = 1;
    private final String lyric = "Jessica's For Forever, This Way.";
    private String verseNoPunc;


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
        removePunc(lyric);
        lyricText.setText(lyric);
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
                    String text = result.get(0);
                    if(text.equals(verseNoPunc)) {
                        txtOutput.setText(text);
                    } else {
                        txtOutput.setText("Not Quite..." + text);
                    }

                }
                break;
            }
        }
    }

    public void removePunc(String lyric){
        String [] words = lyric.replaceAll("[^a-zA-Z' ]", "").toLowerCase().split("\\s+");
        verseNoPunc = TextUtils.join(" ", words);
    }
}
