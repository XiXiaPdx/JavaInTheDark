package com.blueoxgym.javainthedark.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @Override
    public VerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_verse, parent, false);
        VerseViewHolder viewHolder = new VerseViewHolder(view);
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

    public class VerseViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.verseTextView) TextView verseTextView;
        @BindView(R.id.singleVerseCardView) CardView singleVerseCard;
        public VerseViewHolder (View v){
            super(v);
            ButterKnife.bind(this, v);
            mContext= itemView.getContext();
        }

    }

    public VerseAdapter (List songVerses){
        this.songVerses = songVerses;

    }
}
