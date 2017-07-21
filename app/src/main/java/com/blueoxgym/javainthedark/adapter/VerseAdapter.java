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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by macbook on 7/12/17.
 */

public class VerseAdapter extends RecyclerView.Adapter<VerseAdapter.VerseViewHolder> {
    private List<String> songVerses;
   private Context mContext;
    private CallMainLoadFragment callMainLoadFragment;
    private String savedOriginalVerse;
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

    public VerseAdapter (List songVerses, MainActivity activity, RecyclerView versesRecycler){
        this.songVerses = songVerses;
        mContext = activity;
        this.versesRecycler = versesRecycler;
    }

    @Override
    public VerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_verse, parent, false);
        viewHolder = new VerseViewHolder(view);
        this.callMainLoadFragment = (CallMainLoadFragment) mContext;
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
            viewHolder.verseTextView.setText(savedOriginalVerse);
        }
    }

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
            savedOriginalVerse = songVerses.get(getAdapterPosition());
            startLevel();
        }


        //**********
//  below is about starting Level
// **********

        //TODO: refactor this into a whole separate file in the future.
        public void startLevel() {
            gameOn=true;
            int position = getAdapterPosition();
            //what is the current Level of the verse?
            currentLevel = sharedPreferences.getInt(String.valueOf(position), -1);
            //make verse into a string and store in lyric
            lyric = songVerses.get(position);
            setVerseNoPunc(lyric);
            //split lyric string into array of strings
            displayWords = new ArrayList<String>();
            referenceWords = lyric.split(" ");
            //create hint words based on level
            for (int i = 0; i < referenceWords.length; i++) {
                String tempWord = referenceWords[i];
                setHintWords(tempWord);
            }
            setLyricTextView();
        }

        public void setLyricTextView() {
            previousDisplayWords = displayWords;
            String displayWordsIntoString = TextUtils.join(" ", displayWords);
            songVerses.set((getAdapterPosition()), displayWordsIntoString);
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
                                // add "-"
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
                case 4:
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

    public  interface CallMainLoadFragment {
        void loadFragmentCall(String verse);
    }


}
