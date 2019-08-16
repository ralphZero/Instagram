package com.ralph.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ralph.instagram.adapters.ShowUserAdapter;
import com.ralph.instagram.models.Post;

import java.util.ArrayList;
import java.util.List;

public class ShowUserActivity extends AppCompatActivity {

    TextView tvShowUsername,tvShowPostCount,tvShowFollowerCount,tvShowFollowingCount,tvShowName,tvShowBio;
    ImageView ivShowAvatar;
    Button btnFollow,btnUnfollow;
    String objectId;
    ParseUser user;
    RecyclerView rvShowPost;
    ShowUserAdapter adapter;
    List<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);
        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        objectId = getIntent().getStringExtra("ObjectId");

        tvShowUsername = findViewById(R.id.tvShowUsername);
        tvShowPostCount = findViewById(R.id.tvShowPostCount);
        tvShowFollowerCount = findViewById(R.id.tvShowFollowersCount);
        tvShowFollowingCount = findViewById(R.id.tvShowFollowingCount);
        tvShowName = findViewById(R.id.tvShowName);
        tvShowBio = findViewById(R.id.tvShowBio);

        rvShowPost = findViewById(R.id.rvShowPost);

        posts = new ArrayList<>();
        adapter = new ShowUserAdapter(ShowUserActivity.this,posts);
        rvShowPost.setLayoutManager(new GridLayoutManager(ShowUserActivity.this,3, RecyclerView.VERTICAL,false));
        rvShowPost.setAdapter(adapter);

        ivShowAvatar = findViewById(R.id.imageView1);
        btnFollow = findViewById(R.id.btnFollow);
        btnFollow.setVisibility(View.GONE);
        btnUnfollow = findViewById(R.id.btnUnfollow);
        btnUnfollow.setVisibility(View.GONE);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId",objectId);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e!=null){
                    Log.d("UserQuery","Error "+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                user = objects.get(0);
                initBinding();

                //show all post
                ParseQuery<Post> queryPost = new ParseQuery<Post>(Post.class);
                queryPost.whereEqualTo("user",user);
                queryPost.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> objects, ParseException e) {
                        if(e!=null){
                            e.printStackTrace();
                            return;
                        }
                        adapter.addAlltoList(objects);
                    }
                });

            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseObject parseObject = new ParseObject("Followers");
                parseObject.put("user",ParseUser.getCurrentUser());
                parseObject.put("following",user);
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Log.d("Follow","Err: "+e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        getFollowingAndFollowersCount();
                        btnFollow.setVisibility(View.GONE);
                        btnUnfollow.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        btnUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Followers");
                parseQuery.whereEqualTo("user",ParseUser.getCurrentUser());
                parseQuery.whereEqualTo("following",user);
                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e != null){
                            Log.d("Unfollow","Err: "+e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        objects.get(0).deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e != null){
                                    Log.d("Unfollow","Err: "+e.getMessage());
                                    e.printStackTrace();
                                    return;
                                }
                                getFollowingAndFollowersCount();
                                btnUnfollow.setVisibility(View.GONE);
                                btnFollow.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }
        });

    }

    private void initBinding() {
        tvShowUsername.setText(user.getUsername());
        tvShowName.setText(user.getString("name"));
        tvShowBio.setText(user.getString("bio"));
        //get avatar img
        Glide.with(ShowUserActivity.this)
                .load(user.getParseFile("avatar").getUrl())
                .into(ivShowAvatar);

        //get post count
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Post");
        query.whereEqualTo("user",user);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e != null) {
                    Log.d("Count", "CountError");
                    e.printStackTrace();
                    return;
                }
                tvShowPostCount.setText(String.valueOf(count));
            }
        });

        //check if current user followed this user
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Followers");
        parseQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        parseQuery.whereEqualTo("following",user);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e!=null){
                    Log.d("FollowerError","Error = "+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                if(!user.getObjectId().contentEquals(ParseUser.getCurrentUser().getObjectId())){
                    if(objects.size()!=0){
                        Log.d("FollowerError","Not empty");
                        btnUnfollow.setVisibility(View.VISIBLE);
                        btnFollow.setVisibility(View.GONE);
                    }else{
                        Log.d("FollowerError","Empty");
                        btnUnfollow.setVisibility(View.INVISIBLE);
                        btnFollow.setVisibility(View.GONE);
                    }
                }
            }
        });

        getFollowingAndFollowersCount();


    }

    private void getFollowingAndFollowersCount() {
        //get follower count
        ParseQuery<ParseObject> queryFollowerCount = ParseQuery.getQuery("Followers");
        queryFollowerCount.whereEqualTo("following",user);
        queryFollowerCount.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e!=null){
                    Log.d("FollowerCount","Error "+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                tvShowFollowerCount.setText(String.valueOf(count));
            }
        });

        //get following count
        ParseQuery<ParseObject> queryFollowingCount = ParseQuery.getQuery("Followers");
        queryFollowingCount.whereEqualTo("user",user);
        queryFollowingCount.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e!=null){
                    Log.d("FollowingCount","Error "+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                tvShowFollowingCount.setText(String.valueOf(count));
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
