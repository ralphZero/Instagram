package com.ralph.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;
import com.ralph.instagram.PostsActivity;
import com.ralph.instagram.R;
import com.ralph.instagram.models.Post;

import java.util.List;

public class MeAdapter extends RecyclerView.Adapter<MeAdapter.ViewHolder> {

    public Context context;
    public List<Post> list;

    public MeAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.list = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_img,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Post post = list.get(position);
        Glide.with(context)
                .load(post.getImage().getUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .override(150,150)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostsActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("objectId", ParseUser.getCurrentUser().getObjectId());
                context.startActivity(intent);
            }
        });
    }

    public void addAlltoList(List<Post> listp){
        list.clear();
        list.addAll(listp);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
        }
    }
}
