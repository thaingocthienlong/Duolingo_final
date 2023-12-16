package com.app.duolingo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.duolingo.R;
import com.app.duolingo.models.Folder;
import com.app.duolingo.models.Topic;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.VH> {
    public interface OnListener {
        void onClickItem(Topic topic);
        void onLongClick(Topic topic);
    }

    private OnListener onListener;

    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    private List<Topic> topics = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void addList(List<Topic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Topic topic = topics.get(holder.getAdapterPosition());
        holder.tvName.setText(topic.getTitle());
        holder.tvDes.setText(topic.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListener.onClickItem(topic);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onListener.onLongClick(topic);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvDes;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDes = itemView.findViewById(R.id.tvDes);
        }
    }
}
