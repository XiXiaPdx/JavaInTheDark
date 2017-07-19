package com.blueoxgym.javainthedark.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueoxgym.javainthedark.Fragments.LevelTwo;
import com.blueoxgym.javainthedark.MainActivity;
import com.blueoxgym.javainthedark.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by macbook on 7/12/17.
 */

public class VerseAdapter extends RecyclerView.Adapter<VerseAdapter.VerseViewHolder> {
    private List<String> songVerses;
   private Context mContext;
    private CallMainLoadFragment callMainLoadFragment;


    @Override
    public VerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_verse, parent, false);
        VerseViewHolder viewHolder = new VerseViewHolder(view);
        this.callMainLoadFragment = (CallMainLoadFragment) mContext;
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

    public class VerseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.verseTextView) TextView verseTextView;
        @BindView(R.id.singleVerseCardView) CardView singleVerseCard;
        public VerseViewHolder (View v){
            super(v);
            ButterKnife.bind(this, v);
            singleVerseCard.setOnClickListener(this);
            mContext= itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            songVerses.set((getAdapterPosition()), "Test test test");
            notifyItemChanged(getAdapterPosition());
//            String verse = songVerses.get(getAdapterPosition());
//            callMainLoadFragment.loadFragmentCall(verse);

        }
    }

    public VerseAdapter (List songVerses, MainActivity activity){
        this.songVerses = songVerses;
        mContext = activity;
    }

    public static interface CallMainLoadFragment {
        void loadFragmentCall(String verse);
    }
}
