package com.app.duolingo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.duolingo.R;
import com.app.duolingo.models.WordTopic;

import java.util.ArrayList;
import java.util.List;

public class WordTopicAdapter extends RecyclerView.Adapter<WordTopicAdapter.VH> {
    public interface OnListener {
        void onSpeech(WordTopic wordTopic);

        void onLongClick(WordTopic wordTopic);
    }

    private OnListener onListener;

    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    private List<WordTopic> wordList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void addList(List<WordTopic> wordList) {
        this.wordList = wordList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_topic, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        WordTopic wordTopic = wordList.get(holder.getAdapterPosition());
        holder.tvEnglish.setText(wordTopic.getEnglish());
        holder.tvPronounce.setText(wordTopic.getPronounce());
        holder.tvMeaning.setText(wordTopic.getMeaning());
        holder.btnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListener.onSpeech(wordTopic);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onListener.onLongClick(wordTopic);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView tvEnglish;
        private TextView tvPronounce;
        private TextView tvMeaning;
        private ImageButton btnSpeech;


        public VH(@NonNull View itemView) {
            super(itemView);
            tvEnglish = itemView.findViewById(R.id.tvEnglish);
            tvPronounce = itemView.findViewById(R.id.tvPronounce);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
            btnSpeech = itemView.findViewById(R.id.btnSpeech);


        }
    }
}
