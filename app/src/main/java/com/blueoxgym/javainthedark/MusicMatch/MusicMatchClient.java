package com.blueoxgym.javainthedark.MusicMatch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.blueoxgym.javainthedark.Constants.MUSIC_MATCH_KEY;

/**
 * Created by macbook on 7/8/17.
 */

public interface MusicMatchClient {
    @GET("track.lyrics.get")
    Call <LyricsModel> songLyrics(
            @Query("track_id") String songID,
            @Query ("apikey") String apiKey
    );

    @GET("track.search")
    Call <LyricsModel> songSearch(
            @Query("q_track_artist") String songSearchTerms,
            @Query ("apikey") String apiKey,
            @Query("f_has_lyrics") String hasLyrics,
            @Query("page_size") String numberOfResults

    );

}
