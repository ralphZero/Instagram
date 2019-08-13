package com.ralph.instagram.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ralph.instagram.R;
import com.ralph.instagram.models.Post;

import java.net.ProtocolFamily;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.mViewHolder> {

    Context context;
    List<Post> list;
    public HomeAdapter(Context context, List<Post> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_post_img,parent,false);
        mViewHolder viewHolder = new mViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        Post post = list.get(position);

        if(post.getLiked()){
            holder.ibLike.setImageResource(R.drawable.ic_vector_heart);
        }else{
            holder.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
        }

        holder.tvUsername.setText(post.getUser().getUsername());
        holder.tvUsernameComment.setText(post.getUser().getUsername());
        holder.ivDescription.setText(post.getDescription());

        Glide.with(context)
                .load(post.getParseUser("user").getParseFile("avatar").getUrl())
                .apply(new RequestOptions().centerInside().transform(new RoundedCorners(100)))
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(holder.ivAvatar);

        Glide.with(context)
                .load(post.getImage().getUrl())
                .apply(new RequestOptions().centerInside())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(holder.ivImgPost);

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAllToList(List<Post> mList){
        list.clear();
        list.addAll(mList);
        notifyDataSetChanged();
    }


    public class mViewHolder extends RecyclerView.ViewHolder{

        public TextView tvUsername;
        public ImageView ivAvatar;
        public ImageView ivImgPost;
        public TextView tvUsernameComment;
        public TextView ivDescription;
        public ImageButton ibLike;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username_i);
            ivAvatar = itemView.findViewById(R.id.iv_avatar_i);
            ivImgPost = itemView.findViewById(R.id.iv_post_i);
            ivDescription = itemView.findViewById(R.id.tv_comment);
            tvUsernameComment = itemView.findViewById(R.id.tv_usename_comment_i);
            ibLike = itemView.findViewById(R.id.ib_like_i);
        }
    }
}
