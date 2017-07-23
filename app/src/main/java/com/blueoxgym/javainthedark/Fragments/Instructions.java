package com.blueoxgym.javainthedark.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blueoxgym.javainthedark.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class Instructions extends Fragment {

@BindView(R.id.verseInstructions) TextView instructionsText;
    public Instructions() {
        // Required empty public constructor
    }

    public static Instructions newInstance (){
       Instructions instructionsFragment = new Instructions();
        return instructionsFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_instructions, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
