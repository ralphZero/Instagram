package com.ralph.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.ralph.instagram.adapters.AllUserPostAdapter;
import com.ralph.instagram.models.Post;

import java.util.ArrayList;
import java.util.List;

public class PostsActivity extends AppCompatActivity {

    RecyclerView rvAllUserPost;
    AllUserPostAdapter allUserPostAdapter;
    List<Post> postList;
    ParseUser user;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        Toolbar toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("Posts");

        rvAllUserPost = findViewById(R.id.rvAllUserPost);
        postList = new ArrayList<>();
        allUserPostAdapter = new AllUserPostAdapter(PostsActivity.this,postList);
        rvAllUserPost.setLayoutManager(new LinearLayoutManager(PostsActivity.this,RecyclerView.VERTICAL,false));
        rvAllUserPost.setAdapter(allUserPostAdapter);

        position = getIntent().getIntExtra("position",99);

        try {
            user = ParseUser.getQuery().get(getIntent().getStringExtra("objectId"));
            final ParseQuery<Post> parseQuery = new ParseQuery<Post>(Post.class);
            parseQuery.include(Post.KEY_USER);
            parseQuery.whereEqualTo("user", user);
            parseQuery.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> objects, ParseException e) {
                    if(e!=null){
                        e.printStackTrace();
                        return;
                    }
                    allUserPostAdapter.addAllToList(objects);
                    rvAllUserPost.scrollToPosition(position);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
