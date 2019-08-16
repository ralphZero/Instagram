package com.ralph.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ralph.instagram.adapters.CommentAdapter;
import com.ralph.instagram.models.Comment;
import com.ralph.instagram.models.Post;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    ImageView ivMyAvatar;
    EditText etComment;
    Button btnPostComment;

    ImageView ivPostAvatar;
    TextView tvPostComment;
    TextView tvTimestamp;

    RecyclerView rvComments;
    CommentAdapter adapter;
    List<Comment> list;

    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("Comments");

        ivMyAvatar = findViewById(R.id.ivMyAvatar);
        etComment = findViewById(R.id.etComment);
        btnPostComment = findViewById(R.id.btnPostComment);

        ivPostAvatar = findViewById(R.id.iv_avatar_comment);
        tvPostComment = findViewById(R.id.tv_text_comment);
        tvTimestamp = findViewById(R.id.tv_timestamp_comment);

        rvComments = findViewById(R.id.rvComment);
        list = new ArrayList<>();
        adapter = new CommentAdapter(CommentActivity.this,list);
        rvComments.setLayoutManager(new LinearLayoutManager(CommentActivity.this,RecyclerView.VERTICAL,false));
        rvComments.setAdapter(adapter);

        initBinding();

        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mComment = etComment.getText().toString();
                if(mComment.isEmpty()){
                    Snackbar.make(view,"Please add a comment.",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Comment comment = new Comment();
                comment.setUser(ParseUser.getCurrentUser());
                comment.setPost(post);
                comment.setComment(mComment);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            Log.d("save",e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        //fetch all comments for this post again
                        populateRecyclerView();
                        //empty edittext
                        etComment.setText("");
                    }
                });
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Comment");
                query.whereEqualTo("post",post);
                query.countInBackground(new CountCallback() {
                    @Override
                    public void done(final int count, ParseException e) {
                        if(e!=null){
                            e.printStackTrace();
                            return;
                        }
                        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Post");
                        parseQuery.getInBackground(post.getObjectId(), new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if(e!=null){
                                    e.printStackTrace();
                                    return;
                                }
                                object.put("commentcount",count);
                                try {
                                    object.save();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void initBinding() {
        ParseQuery<Post> parseQuery = new ParseQuery<Post>(Post.class);
        parseQuery.include(Post.KEY_USER);
        parseQuery.whereEqualTo("objectId",getIntent().getStringExtra("post"));
        parseQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e!=null){
                    Log.d("FindException",e.getMessage());
                    e.printStackTrace();
                    return;
                }
                post = posts.get(0);
                Glide.with(CommentActivity.this)
                        .load(post.getUser().getParseFile("avatar").getUrl())
                        .into(ivPostAvatar);

                StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(post.getUser().getUsername());
                stringBuilder.setSpan(bold,0,stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.append(" ");
                stringBuilder.append(post.getString("description"));
                tvPostComment.setText(stringBuilder);
                tvTimestamp.setText(TimeFormatter.getTimeDifference(post.getCreatedAt().toString()));

                Glide.with(CommentActivity.this)
                        .load(ParseUser.getCurrentUser().getParseFile("avatar").getUrl())
                        .into(ivMyAvatar);

                populateRecyclerView();
            }
        });
    }

    private void populateRecyclerView() {
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
                adapter.addAllToList(objects);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
