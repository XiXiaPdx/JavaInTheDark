package com.blueoxgym.javainthedark.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blueoxgym.javainthedark.Fragments.VersesList;
import com.blueoxgym.javainthedark.MainActivity;
import com.blueoxgym.javainthedark.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by macbook on 7/12/17.
 */

public class VerseAdapter extends RecyclerView.Adapter<VerseAdapter.VerseViewHolder> {
    private List<String> songVerses;
    private ArrayList<String> originalSongVerses = new ArrayList<String>();
   private Context mContext;
    public String savedOriginalVerse;
    public int savedOriginalPosition;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    //startLevel related
    private String lyric;
    private String verseNoPunc;
    private int currentLevel;
    private String[] referenceWords;
    private ArrayList displayWords;
    private ArrayList previousDisplayWords;
    private RecyclerView versesRecycler;
    private Boolean gameOn = false;
    VerseViewHolder viewHolder;

    public VerseAdapter (List<String> songVerses, MainActivity activity, RecyclerView versesRecycler){
        this.songVerses = songVerses;
        mContext = activity;
        this.versesRecycler = versesRecycler;
        for(int i=0; i < songVerses.size(); i++){
            originalSongVerses.add(i, songVerses.get(i));
        }
    }

    @Override
    public VerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_verse, parent, false);
        viewHolder = new VerseViewHolder(view);
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(mContext);
        editor=sharedPreferences.edit();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VerseAdapter.VerseViewHolder holder, int position) {
        holder.verseTextView.setText(songVerses.get(position));
    }

    @Override
    public int getItemCount() {
        return songVerses.size();
    }

    public void resetVerse(){
        if (gameOn) {
            gameOn = false;
            songVerses.set(savedOriginalPosition, originalSongVerses.get(savedOriginalPosition));
            notifyItemChanged(savedOriginalPosition);
        }
    }

    public void checkForMatch(String speech) {
        if (speech.toLowerCase().equals(verseNoPunc)) {
            currentLevel++;
            editor.putInt(String.valueOf(savedOriginalPosition), currentLevel).apply();
            Log.d("New level now", String.valueOf(sharedPreferences.getInt(String.valueOf(savedOriginalPosition), -1)));
            viewHolder.startLevel();
        } else {
            checkEachWord(speech);
        }
    }

    public void checkEachWord(String speech) {
        String[] speechWords = speech.split(" ");
        //reset displayWord
        displayWords = new ArrayList<String>();
        //compare each word in speech array with reference array
        for (int i = 0; i < referenceWords.length; i++) {
            //speaker can say too few words. Check to prevent indexOutofBounds
            if (i < speechWords.length) {
                // need to remove punctuation from this word
                String currentWordNoPunc = viewHolder.removeWordPunc(referenceWords[i]);
                Log.d("CURRENT WORD", currentWordNoPunc);
                String speechWord = speechWords[i].toLowerCase();
                if (speechWord.equals(currentWordNoPunc)
                        ) {
                    //if word is match, pull original from reference WHICH HAS PUNC and add to display
                    displayWords.add(referenceWords[i]);
                } else {
                    // word doesn't match, reveal more hints.
                    revealMoreLetters(i);
                }
            } else {
                //speech too short as compared to reference
//                revealMoreLetters(i);
                viewHolder.setHintWords(referenceWords[i]);
            }
        }
        viewHolder.setLyricTextView();
    }

    public void revealMoreLetters(int i) {
        // We know speech did not equal reference
        // use previousDisplayWords to know where to begin inserting new letter
        String previousWord = previousDisplayWords.get(i).toString();
        if (viewHolder.alreadySolved(previousWord)) {
            viewHolder.setHintWords(previousWord);
            return;
        }
        StringBuilder tempWord = new StringBuilder(previousWord);
        int dashCount = 0;
        for (int k = 0; k < previousWord.length(); k++) {
            if (String.valueOf(previousWord.charAt(k)).equals("-")) {
                dashCount++;
            }
        }
        switch (currentLevel) {
            case 1:
                if (dashCount > 1) {
                    for (int j = 0; j < previousWord.length(); j++) {
                        if (String.valueOf(previousWord.charAt(j)).equals("-")) {
                            tempWord.setCharAt(j, referenceWords[i].charAt(j));
                            displayWords.add(tempWord);
                            break;
                        }
                    }
                } else {
                    displayWords.add(previousWord);
                }
                break;
            case 2:
            case 4:
                displayWords.add(previousWord);
                break;
            case 3:
                if (dashCount > 1) {
                    Random ran = new Random();
                    int randomIndex = 0;
                    Character randomLetter;
                    do {
                        randomIndex = ran.nextInt(referenceWords[i].length());
                        randomLetter = referenceWords[i].charAt(randomIndex);
                    }
                    while (randomIndex == 0 || viewHolder.ifEndInPunc(randomLetter) || viewHolder.isAtoZ(tempWord.charAt(randomIndex)));
                    tempWord.setCharAt(randomIndex, randomLetter);
                    displayWords.add(tempWord);
                } else {
                    displayWords.add(previousWord);
                }
                break;
        }
    }


    // VerseViewHolder Class starts here
    public class VerseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.verseTextView)
        TextView verseTextView;
        @BindView(R.id.singleVerseCardView)
        CardView singleVerseCard;

        public VerseViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            singleVerseCard.setOnClickListener(this);
            mContext = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            if (gameOn == false) {
                savedOriginalPosition = getAdapterPosition();
                gameOn=true;
                startLevel();
            } else {

            }
        }
        //**********
//  below is about starting Level
// **********

        public void startLevel() {
            int position = savedOriginalPosition;
            //what is the current Level of the verse?
            currentLevel = sharedPreferences.getInt(String.valueOf(position), -1);
            Log.d("current level", String.valueOf(currentLevel));
            //make verse into a string and store in lyric
            lyric = originalSongVerses.get(position);
            setVerseNoPunc(lyric);
            //split lyric string into array of strings
            displayWords = new ArrayList<String>();
            referenceWords = lyric.split(" ");
            //create hint words based on level
            for (int i = 0; i < referenceWords.length; i++) {
                String tempWord = referenceWords[i];
                Log.d("Original Word", tempWord );
                setHintWords(tempWord);
            }
            setLyricTextView();
        }

        public void setLyricTextView() {
            previousDisplayWords = displayWords;
            String displayWordsIntoString = TextUtils.join(" ", displayWords);
            Log.d("adapter position set", String.valueOf(savedOriginalPosition));
            songVerses.set(savedOriginalPosition, displayWordsIntoString);
            notifyItemChanged(getAdapterPosition());
        }


        public Boolean alreadySolved(String word) {
            if (word.contains("-")) {
                return false;
            } else {
                return true;
            }
        }

        public Boolean isAtoZ(char letter) {
            if (String.valueOf(letter).matches("[a-zA-Z]")) {
                return true;
            }
            return false;
        }

        public Boolean isOneLetterWord(String tempWord) {
            if (tempWord.length() == 1) {
                displayWords.add("-");
                return true;
            } else if (tempWord.length() == 2 && ifEndInPunc(tempWord.charAt(1))) {
                // corner case   "I,"
                displayWords.add("-" + tempWord.charAt(1));
                return true;
            } else {
                return false;
            }
        }

        public void setVerseNoPunc(String lyric) {
            String[] words = lyric.replaceAll("[^a-zA-Z' ]", "").toLowerCase().split("\\s+");
            verseNoPunc = TextUtils.join(" ", words);
        }

        public String removeWordPunc(String word) {
            return word.replaceAll("[^a-zA-Z' ]", "").toLowerCase();
        }

        public Boolean ifEndInPunc(Character letter) {
            Pattern p = Pattern.compile("[,.?!:']");
            Matcher m = p.matcher(String.valueOf(letter));
            if (m.find()) {
                return true;
            }
            return false;
        }

        public void setHintWords(String word) {
            String tempWord = removeWordPunc(word);
            String newDisplayWord = "";
            switch (currentLevel) {
                case 1:
                case 3:
                    if (isOneLetterWord(tempWord)) {
                        break;
                    } else {
                        //loop over it and create new word character by character
                        for (int j = 0; j < tempWord.length(); j++) {
                            // display first letter of word
                            if (j == 0 && currentLevel == 1) {
                                newDisplayWord = newDisplayWord + tempWord.charAt(j);
                            } else {
                                // level 3, no first letter, add "-"
                                if (isAtoZ(tempWord.charAt(j))) {
                                    newDisplayWord = newDisplayWord + "-";
                                } else {
                                    // keep punctuation
                                    newDisplayWord = newDisplayWord + tempWord.charAt(j);
                                }
                            }
                        }
                        displayWords.add(newDisplayWord);
                    }
                    break;
                case 2:
                    //just the first letter only
                case 4:
                    //level 4, just a blank for each word
                    for (int j = 0; j < tempWord.length(); j++) {
                        // display first letter of word
                        if (j == 0) {
                            switch (currentLevel) {
                                case 2:
                                    newDisplayWord = newDisplayWord + tempWord.charAt(j);
                                    break;
                                case 4:
                                    newDisplayWord = newDisplayWord + "-";
                                    break;
                            }
                            //checking if there is punctuation at end of word
                        } else if (j == (tempWord.length() - 1) && ifEndInPunc(tempWord.charAt(j))) {
                            newDisplayWord = newDisplayWord + tempWord.charAt(j);
                        }
                    }
                    displayWords.add(newDisplayWord);
                    break;
            }
        }
    }

    // END of VerseView Holder

}
