package com.ralph.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ralph.instagram.CommentActivity;
import com.ralph.instagram.R;
import com.ralph.instagram.ShowUserActivity;
import com.ralph.instagram.TimeFormatter;
import com.ralph.instagram.models.Comment;
import com.ralph.instagram.models.Like;
import com.ralph.instagram.models.Post;

import java.util.ArrayList;
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
    public void onBindViewHolder(@NonNull final mViewHolder holder, final int position) {
        final Post post = list.get(position);

        if(getLiked(post)){
            holder.ibLike.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.medium_red)));
            holder.ibLike.setImageResource(R.drawable.ufi_heart_active);
        }else{
            holder.ibLike.setImageResource(R.drawable.ufi_heart);
        }

        holder.tvLikeCounter.setText(String.valueOf(post.getLikeCount()));

        holder.tvUsername.setText(post.getUser().getUsername());
        holder.tvUsernameComment.setText(post.getUser().getUsername());
        holder.ivDescription.setText(post.getDescription());
        holder.tvTimeStamp.setText(TimeFormatter.getTimeDifference(post.getCreatedAt().toString()));

        Glide.with(context)
                .load(post.getParseUser("user").getParseFile("avatar").getUrl())
                .apply(new RequestOptions().centerInside().transform(new RoundedCorners(100)))
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(holder.ivAvatar);

        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserInfo(post);
            }
        });
        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserInfo(post);
            }
        });

        Glide.with(context)
                .load(post.getImage().getUrl())
                .apply(new RequestOptions().centerInside())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(holder.ivImgPost);

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getLiked(post)){
                    //we clicked to unlike
                    holder.ibLike.setImageResource(R.drawable.ufi_heart);
                    holder.ibLike.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(android.R.color.black)));

                    holder.tvLikeCounter.setText(String.valueOf(post.getLikeCount() - 1));
                    unlikeThisPost(post);
                }else{
                    //we clicked to like
                    holder.ibLike.setImageResource(R.drawable.ufi_heart_active);
                    holder.ibLike.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.medium_red)));

                    holder.tvLikeCounter.setText(String.valueOf(post.getLikeCount() + 1));
                    likeThisPost(post);
                }
            }
        });

        holder.ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("post", post.getObjectId());
                context.startActivity(intent);
            }
        });

        //view all comments implementation
        switch (post.getCommentCount()){
            case 0:
                holder.tvViewComment.setVisibility(View.GONE);
                holder.tvComment2.setVisibility(View.GONE);
                holder.tvComment1.setVisibility(View.GONE);
                break;
            case 1:
                holder.tvViewComment.setVisibility(View.GONE);
                holder.tvComment2.setVisibility(View.GONE);
                holder.tvComment1.setVisibility(View.VISIBLE);

                ParseQuery<Comment> query = new ParseQuery<Comment>(Comment.class);
                query.include(Comment.KEY_USER);
                query.whereEqualTo("post",post);
                query.orderByDescending("objectId");
                query.findInBackground(new FindCallback<Comment>() {
                    @Override
                    public void done(List<Comment> objects, ParseException e) {
                        if(e!=null){
                            e.printStackTrace();
                            return;
                        }
                        StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(objects.get(0).getUser().getUsername());
                        stringBuilder.setSpan(bold,0,stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        stringBuilder.append(" ");
                        stringBuilder.append(objects.get(0).getComment());

                        holder.tvComment1.setText(stringBuilder);
                    }
                });

                break;
            case 2:
                holder.tvViewComment.setVisibility(View.GONE);
                holder.tvComment2.setVisibility(View.VISIBLE);
                holder.tvComment1.setVisibility(View.VISIBLE);

                ParseQuery<Comment> query1 = new ParseQuery<Comment>(Comment.class);
                query1.include(Comment.KEY_USER);
                query1.whereEqualTo("post",post);
                query1.orderByDescending("objectId");
                query1.findInBackground(new FindCallback<Comment>() {
                    @Override
                    public void done(List<Comment> objects, ParseException e) {
                        if(e!=null){
                            e.printStackTrace();
                            return;
                        }

                        StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(objects.get(0).getUser().getUsername());
                        stringBuilder.setSpan(bold,0,stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        stringBuilder.append(" ");
                        stringBuilder.append(objects.get(0).getComment());

                        holder.tvComment1.setText(stringBuilder);

                        StyleSpan boldd = new StyleSpan(android.graphics.Typeface.BOLD);
                        SpannableStringBuilder stringBuilder1 = new SpannableStringBuilder(objects.get(1).getUser().getUsername());
                        stringBuilder1.setSpan(bold,0,stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        stringBuilder1.append(" ");
                        stringBuilder1.append(objects.get(1).getComment());

                        holder.tvComment2.setText(stringBuilder1);
                    }
                });

                break;
                default:
                    holder.tvViewComment.setVisibility(View.VISIBLE);
                    holder.tvComment2.setVisibility(View.GONE);
                    holder.tvComment1.setVisibility(View.GONE);

                    holder.tvViewComment.setText("View all "+post.getCommentCount()+" comments");

                    break;
        }

        holder.rlComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("post", post.getObjectId());
                context.startActivity(intent);
            }
        });

    }

    private void showUserInfo(Post post) {
        String id = post.getUser().getObjectId();
        Intent intent = new Intent(context, ShowUserActivity.class);
        intent.putExtra("ObjectId",id);
        context.startActivity(intent);
    }

    private boolean getLiked(Post post) {
        //final Boolean[] result = new Boolean[1];
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Like");
        query.whereEqualTo("user",ParseUser.getCurrentUser());
        query.whereEqualTo("post",post);
        try {
            if(query.count()!=0)
                return true;
            else
                return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void unlikeThisPost(final Post post) {
        final int[] countLike = new int[1];
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Like");
        query.whereEqualTo("post",post);
        query.whereEqualTo("user",ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e != null){
                    Log.d("Unlike","error unlike"+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                objects.get(0).deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            Log.d("Unlike","Error delete unlike"+e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        Log.d("Unlike","Success");
                        ParseQuery<ParseObject> l = new ParseQuery<ParseObject>("Like");
                        l.whereEqualTo("post",post);
                        try {
                            countLike[0] = l.count();
                            Log.e("LikeCount","Il y a "+countLike[0]);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        ParseQuery<ParseObject> postQuery = new ParseQuery<ParseObject>("Post");
                        postQuery.getInBackground(post.getObjectId(), new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if(e!=null){
                                    Log.d("Unlike","Error post delete unlike" + e.getMessage());
                                    e.printStackTrace();
                                    return;
                                }
                                object.put("liked",false);
                                object.put("likecount",countLike[0]);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e!=null){
                                            Log.d("Unlike","Error save unlike" + e.getMessage());
                                            e.printStackTrace();
                                            return;
                                        }
                                        post.setLikeCount(countLike[0]);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void likeThisPost(final Post post) {
        final int[] countLike = new int[1];
        //we save the like
        Like likeObject = new Like();
        likeObject.setUser(ParseUser.getCurrentUser());
        likeObject.setPost(post);
        likeObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.d("Like","Error Like" + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                Log.d("Like","Success");
                //we count the likes for this post
                ParseQuery<ParseObject> l = new ParseQuery<ParseObject>("Like");
                l.whereEqualTo("post",post);
                try {
                    countLike[0] = l.count();
                    Log.e("LikeCount","Il y a "+countLike[0]);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Post");
                query.getInBackground(post.getObjectId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e != null) {
                            Log.d("Like","Error Query Like" + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        object.put("liked",true);
                        object.put("likecount",countLike[0]);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e!=null){
                                    Log.d("Like","Error save Like" + e.getMessage());
                                    e.printStackTrace();
                                    return;
                                }
                                post.setLikeCount(countLike[0]);
                            }
                        });
                    }
                });

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
        public TextView tvTimeStamp;
        public TextView ivDescription;
        public TextView tvLikeCounter;
        public ImageButton ibLike;
        public ImageButton ibComment;
        public TextView tvViewComment;
        public TextView tvComment1;
        public TextView tvComment2;
        public RelativeLayout rlComment;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username_i);
            ivAvatar = itemView.findViewById(R.id.iv_avatar_i);
            ivImgPost = itemView.findViewById(R.id.iv_post_i);
            ivDescription = itemView.findViewById(R.id.tv_comment);
            tvUsernameComment = itemView.findViewById(R.id.tv_usename_comment_i);
            ibLike = itemView.findViewById(R.id.ib_like_i);
            ibComment = itemView.findViewById(R.id.ib_comment_i);
            tvLikeCounter = itemView.findViewById(R.id.tvViewCounter);
            tvTimeStamp = itemView.findViewById(R.id.tvTimestamp);
            tvViewComment = itemView.findViewById(R.id.tvViewComment);
            tvComment1 = itemView.findViewById(R.id.tvComment1);
            tvComment2 = itemView.findViewById(R.id.tvComment2);
            rlComment = itemView.findViewById(R.id.rl_comment);
        }
    }
}
