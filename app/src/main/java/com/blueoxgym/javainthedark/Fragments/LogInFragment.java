package com.blueoxgym.javainthedark.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueoxgym.javainthedark.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogInFragment extends Fragment {


    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    public static LogInFragment newInstance(){
        LogInFragment newFragment = new LogInFragment();
        return newFragment;
    }

}
