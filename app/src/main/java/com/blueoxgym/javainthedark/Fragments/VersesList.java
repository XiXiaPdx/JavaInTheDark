package com.blueoxgym.javainthedark.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class VersesList extends Fragment {
    @BindView(R.id.versesRecycleView) RecyclerView versesRecycleView;
    @BindView(R.id.songNameTextView) TextView songName;
    @BindView(R.id.artistTextView)TextView artistName;
    private SharedPreferences mSharedPreferences;
    private String mRecentAddress;
    List verseList;
    List finalModVerseList;

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
        displayArtistAndSongName();
        lyricsToVerseList();
        setVersesIntoRecyclerView();
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
        VerseAdapter verseAdapter = new VerseAdapter(finalModVerseList, (MainActivity)getActivity());
        versesRecycleView.setAdapter(verseAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        versesRecycleView.setLayoutManager(llm);
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(versesRecycleView);
    }
}
