package com.blueoxgym.javainthedark.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blueoxgym.javainthedark.MainActivity;
import com.blueoxgym.javainthedark.MusicMatch.LyricsModel;
import com.blueoxgym.javainthedark.MusicMatch.MusicMatchClient;
import com.blueoxgym.javainthedark.MusicMatch.ServiceGenerator;
import com.blueoxgym.javainthedark.MusicMatch.EachTrack;
import com.blueoxgym.javainthedark.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.blueoxgym.javainthedark.Constants.MUSIC_MATCH_KEY;

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
        getLyricsCall();
        searchForSong();
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

    public void getLyricsCall(){
        MusicMatchClient client = ServiceGenerator.createService(MusicMatchClient.class);
        Call<LyricsModel> call = client.songLyrics("126153559", MUSIC_MATCH_KEY);
        call.enqueue(new Callback<LyricsModel>() {
            @Override
            public void onResponse(Call<LyricsModel> call, Response<LyricsModel> response) {
                if (response.code() == 200) {
                    LyricsModel songLyrics = response.body();
                    Log.d("SUCCESS", songLyrics.getMessage().getBody().getLyrics().getLyrics_body().toString());

                }
            }

            @Override
            public void onFailure(Call<LyricsModel> call, Throwable t) {
                Log.d("FAILED", "NOOOOOOOOOO");

            }
        });
    }

    public void searchForSong(){
        MusicMatchClient client = ServiceGenerator.createService(MusicMatchClient.class);
        Call<LyricsModel> call = client.songSearch("Million Reasons Gaga", MUSIC_MATCH_KEY, "true", "5");
        call.enqueue(new Callback<LyricsModel>() {
            @Override
            public void onResponse(Call<LyricsModel> call, Response<LyricsModel> response) {
                if (response.code() == 200) {
                    List<EachTrack> trackList = response.body().getMessage().getBody().getTrack_list();
                    for (EachTrack track: trackList) {
                        Log.d("SEARCH SEARCH", Integer.toString(track.getTrack().getTrack_id()));
                    }
                }
            }

            @Override
            public void onFailure(Call<LyricsModel> call, Throwable t) {
                Log.d("FAILED", "NOOOOOOOOOO");
            }
        });
    }
}
