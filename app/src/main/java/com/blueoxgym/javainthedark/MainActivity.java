package com.blueoxgym.javainthedark;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
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

import com.blueoxgym.javainthedark.Fragments.Instructions;
import com.blueoxgym.javainthedark.Fragments.LevelTwo;
import com.blueoxgym.javainthedark.Fragments.VersesList;
import com.blueoxgym.javainthedark.Fragments.LogInFragment;
import com.blueoxgym.javainthedark.Fragments.LyricSearch;
import com.blueoxgym.javainthedark.Fragments.MicFragment;
import com.blueoxgym.javainthedark.adapter.VerseAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MicFragment.CallMainLoadVerseFragment, MicFragment.CheckSpeech {


    public static final String TAG = "In Observer";
public FirebaseAuth firebaseAuth;
    public FirebaseAuth.AuthStateListener authListener;
    private String trackName;
    private String artistName;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private FragmentManager fragmentManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();

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

        if(fragment.toString().contains("MicFragment")) {
            fragmentManager.beginTransaction().replace(R.id.content2_frame, fragment).addToBackStack(null).commit();
        } else if (fragment.toString().contains("Instructions")) {
            fragmentManager.beginTransaction().replace(R.id.content2_frame, fragment).addToBackStack(null).commit();
        } else if (fragment.toString().contains("Search")) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("Search").commit();
        } else if (fragment.toString().contains("Verses")) {
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.findFragmentById(R.id.content_frame) instanceof LyricSearch ) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            super.onBackPressed();
        } else {
            fragmentManager.popBackStack("Search", FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
    public void loadVerseFragmentCall(String lyrics) {
        VersesList versesList = new VersesList();
        Instructions instructions = new Instructions();
        loadFragment(versesList.newInstance(lyrics));
        loadFragment(instructions.newInstance());
    }

    @Override
    public void checkingSpeech(String text) {
    }
}
