package com.example.journalapplication;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyJournalAdapter extends RecyclerView.Adapter<MyJournalAdapter.MyViewHolder> {
    private Context context;
    private List<Journal> journalList;

    public MyJournalAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Journal current = journalList.get(position);
        holder.listTitle.setText(current.getTitle());
        holder.listThought.setText(current.getThoughts());
        holder.rowTitle.setText(current.getUserName());
        String imgUrl = current.getImgUrl();
        String timeAgo= (String) DateUtils.getRelativeTimeSpanString(current.getTimestamp().getSeconds()*1000);
        holder.listDate.setText(timeAgo);
        Glide.with(context).load(imgUrl).into(holder.imageList);
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView rowTitle,listTitle,listThought,listDate;
        private ImageButton imgShare;
        private ImageView imageList;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rowTitle = itemView.findViewById(R.id.id_journal_row_username);
            listTitle = itemView.findViewById(R.id.id_journal_title_list);
            listThought = itemView.findViewById(R.id.id_journal_thought_list);
            listDate = itemView.findViewById(R.id.id_journal_timestamp_list);
            imgShare = itemView.findViewById(R.id.id_journal_row_share_button);
            imageList = itemView.findViewById(R.id.id_journal_image_list);
        }
    }
}
