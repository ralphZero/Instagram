package com.ralph.instagram.adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;
import com.ralph.instagram.R;
import com.ralph.instagram.TimeFormatter;
import com.ralph.instagram.models.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Comment> list;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.list = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_comment, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = list.get(position);
        ParseUser user = comment.getUser();

        StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(user.getUsername());
        stringBuilder.setSpan(bold,0,stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append(" ");
        stringBuilder.append(comment.getComment());

        holder.tvTextComment.setText(stringBuilder);
        holder.tvTimeStamp.setText(TimeFormatter.getTimeDifference(comment.getCreatedAt().toString()));
        Glide.with(context)
                .load(user.getParseFile("avatar").getUrl())
                .into(holder.ivUserComment);
    }

    public void addAllToList(List<Comment> comment){
        list.clear();
        list.addAll(comment);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivUserComment;
        public TextView tvTextComment;
        public TextView tvTimeStamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserComment = itemView.findViewById(R.id.ivUserComment);
            tvTextComment = itemView.findViewById(R.id.tvTextComment);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStampComment);
        }
    }
}
