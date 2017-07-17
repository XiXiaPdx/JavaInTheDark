package com.blueoxgym.javainthedark.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueoxgym.javainthedark.MusicMatch.Lyrics;
import com.blueoxgym.javainthedark.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LyricSearch extends Fragment {


    public LyricSearch() {
        // Required empty public constructor
    }

    public static LyricSearch newInstance(){
        LyricSearch lyricSearch = new LyricSearch();
        return lyricSearch;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lyric_search, container, false);
    }

}
