package com.blueoxgym.javainthedark.MusicMatch;

/**
 * Created by macbook on 7/8/17.
 */

public class LyricsModel {
    public LyricsModel () {};
    Body body;
    public Body getbody() {
        return body;
    }
    public class Body{
        public Body (){};
        Lyrics lyrics;
        public Lyrics getLyrics(){
            return lyrics;
        }
        public class Lyrics{
            public Lyrics () {};

           public int lyrics_id;
           public String lyrics_body;

            public int getLyrics_id() {
                return lyrics_id;
            }

            public String getLyrics_body() {
                return lyrics_body;
            }
        }
    }
}




