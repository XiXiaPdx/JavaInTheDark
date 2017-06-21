package com.blueoxgym.javainthedark.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blueoxgym.javainthedark.MainActivity;
import com.blueoxgym.javainthedark.R;
import com.google.firebase.database.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LevelOne extends Fragment implements View.OnClickListener {
        @BindView(R.id.button3) Button button;


    public LevelOne() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_level_one, container, false);
        ButterKnife.bind(this, view);
        button.setOnClickListener(this);
        return view;
    }

    public static LevelOne newInstance(){
        LevelOne levelOneFragment = new LevelOne();
        return levelOneFragment;
    }

    @Override
    public void onClick(View v) {
        if(v == button){
            LevelTwo levelTwo = new LevelTwo();
            //can not just import MainActivity and set to variable as global in this class. Must create a new instance of LoadFragment everytime or crashes on replace fragment
            ((MainActivity)getActivity()).loadFragment(levelTwo.newInstance());
        }
    }
}
