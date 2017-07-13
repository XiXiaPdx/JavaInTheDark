package com.blueoxgym.javainthedark.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blueoxgym.javainthedark.MainActivity;
import com.blueoxgym.javainthedark.MusicMatch.LyricsModel;
import com.blueoxgym.javainthedark.MusicMatch.MusicMatchClient;
import com.blueoxgym.javainthedark.MusicMatch.ServiceGenerator;
import com.blueoxgym.javainthedark.MusicMatch.EachTrack;
import com.blueoxgym.javainthedark.R;
import com.blueoxgym.javainthedark.adapter.VerseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.blueoxgym.javainthedark.Constants.MUSIC_MATCH_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class LevelOne extends Fragment implements View.OnClickListener {
    @BindView(R.id.button3) Button button;
    @BindView(R.id.versesRecycleView) RecyclerView versesRecycleView;
    List verseList;
    List finalModVerseList;


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
        String lyrics = getArguments().getString("lyrics", "");
        verseList = new ArrayList<String>();
        finalModVerseList = new ArrayList<String>();
        lyricsToVerseList(lyrics);
        VerseAdapter verseAdapter = new VerseAdapter(finalModVerseList);
        versesRecycleView.setAdapter(verseAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        versesRecycleView.setLayoutManager(llm);
        return view;
    }

    public static LevelOne newInstance(String lyrics){
        LevelOne levelOneFragment = new LevelOne();
        Bundle args = new Bundle();
        args.putString("lyrics", lyrics);
        levelOneFragment.setArguments(args);
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

    public void lyricsToVerseList(String lyrics){
        verseList= Arrays.asList(lyrics.split("\n"));
        int endOfFinalList=verseList.indexOf("...");
        for (int i = 0; i < endOfFinalList; i++) {
           if(!verseList.get(i).toString().equals("")){
               finalModVerseList.add(verseList.get(i).toString());
           }
        }
    }

}
