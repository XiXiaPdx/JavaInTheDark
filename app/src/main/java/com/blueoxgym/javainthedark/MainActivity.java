package com.blueoxgym.javainthedark;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.blueoxgym.javainthedark.Fragments.LevelTwo;
import com.blueoxgym.javainthedark.Fragments.VersesList;
import com.blueoxgym.javainthedark.Fragments.LogInFragment;
import com.blueoxgym.javainthedark.Fragments.LyricSearch;
import com.blueoxgym.javainthedark.Fragments.MicFragment;
import com.blueoxgym.javainthedark.MusicMatch.EachTrack;
import com.blueoxgym.javainthedark.MusicMatch.LyricsModel;
import com.blueoxgym.javainthedark.MusicMatch.MusicMatchClient;
import com.blueoxgym.javainthedark.MusicMatch.ServiceGenerator;
import com.blueoxgym.javainthedark.adapter.VerseAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.blueoxgym.javainthedark.Constants.ARTIST_NAME;
import static com.blueoxgym.javainthedark.Constants.MUSIC_MATCH_KEY;
import static com.blueoxgym.javainthedark.Constants.SONG_NAME;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VerseAdapter.CallMainLoadFragment {


    public static final String TAG = "In Observer";
public FirebaseAuth firebaseAuth;
    public FirebaseAuth.AuthStateListener authListener;
    private String trackName;
    private String artistName;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        makeAuthListener();
    }

    public void loadFragment(Fragment fragment) {
        // Enter transition set for fragment
        Fade enterFade = new Fade ();
        enterFade.setDuration(500);
        fragment.setEnterTransition(enterFade);
        // set transition fade for exit of fragment
        Fade exitFade = new Fade();
        exitFade.setDuration(200);
        fragment.setExitTransition(exitFade);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragment.toString().contains("MicFragment")) {
            fragmentManager.beginTransaction().replace(R.id.content2_frame, fragment).addToBackStack(null).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
        }
    }

public void makeAuthListener() {
    authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                loadFragment(LogInFragment.newInstance());
            } else {
                loadFragment(LyricSearch.newInstance());
                loadFragment(MicFragment.newInstance());
            }
        }
    };
}


    public void getLyricsCall(int songId){
        MusicMatchClient client = ServiceGenerator.createService(MusicMatchClient.class);
        Observable<LyricsModel> call = client.songLyrics(Integer.toString(songId), MUSIC_MATCH_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        //observer
        Observer<LyricsModel> observer = new Observer<LyricsModel>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe" + Thread.currentThread().getName());
            }

            @Override
            public void onNext(LyricsModel value) {
                loadFragment(VersesList.newInstance(value.getMessage().getBody().getLyrics().getLyrics_body()));
                Log.e(TAG, "onNext: " +  value.getMessage().getBody().getLyrics().getLyrics_body()+ Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: All Done!" + Thread.currentThread().getName());
            }

        };
        call.subscribe(observer);
    }


    public void searchForSong(){
        MusicMatchClient client = ServiceGenerator.createService(MusicMatchClient.class);
        Observable<LyricsModel> call = client.songSearch("Waving Through A Window Ben Platt", MUSIC_MATCH_KEY, "true", "5")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer<LyricsModel> observer = new Observer<LyricsModel>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe" + Thread.currentThread().getName());
            }

            @Override
            public void onNext(LyricsModel value) {
                Log.e(TAG, "onNext: "  + Thread.currentThread().getName());
                List<EachTrack> trackList = value.getMessage().getBody().getTrack_list();
                try {
                    getLyricsCall(trackList.get(0).getTrack().getTrack_id());
                    trackName = trackList.get(0).getTrack().getTrack_name();
                    artistName = trackList.get(0).getTrack().getArtist_name();
                    addSongAndArtistShared(artistName, trackName);

                } catch (IndexOutOfBoundsException e){
                    searchError();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: All Done!" + Thread.currentThread().getName());
            }

        };
        call.subscribe(observer);
    }

    public void addSongAndArtistShared(String artist, String song){
        mEditor.putString(ARTIST_NAME, artist).apply();
        mEditor.putString(SONG_NAME, song).apply();
    }

    public void searchError() {
        Log.d("ERROR", "Sorry, we don't have lyrics for that song.");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.log_in) {
            // go to login screen?
        } else if (id == R.id.log_out) {
            firebaseAuth.signOut();
            try{
                Log.d("Current User", firebaseAuth.getCurrentUser().toString());
            } catch (NullPointerException e){
                Log.d("User Null", "Logged Out");
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void loadFragmentCall(String verse) {
        LevelTwo levelTwo = new LevelTwo();
        loadFragment(levelTwo.newInstance(verse));
    }
}
