package com.app.duolingo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.duolingo.R;
import com.app.duolingo.models.Folder;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.VH> {
    public interface OnListener {
        void onClick(Folder folder);

        void onLongClick(Folder folder);
    }

    private OnListener onListener;

    public void setOnListener(OnListener onListener) {
        this.onListener = onListener;
    }

    private List<Folder> folders = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void addList(List<Folder> folders) {
        this.folders = folders;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Folder folder = folders.get(holder.getAdapterPosition());
        holder.tvName.setText(folder.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListener.onClick(folder);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onListener.onLongClick(folder);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView tvName;


        public VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);


        }
    }
}
