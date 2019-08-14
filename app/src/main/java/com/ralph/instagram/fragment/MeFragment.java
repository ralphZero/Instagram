package com.ralph.instagram.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.parse.CountCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.ralph.instagram.LoginActivity;
import com.ralph.instagram.R;

public class MeFragment extends Fragment {

    TextView tvUsername;
    TextView tvName;
    ImageView imageView;
    TextView tvPostCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_me,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = view.findViewById(R.id.toolbar4);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvUsername = view.findViewById(R.id.tvMeUsername);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvName = view.findViewById(R.id.tvName);
        tvName.setText(ParseUser.getCurrentUser().getString("name"));
        imageView = view.findViewById(R.id.imageView);

        tvPostCount = view.findViewById(R.id.tvPostCount);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Post");
        query.whereEqualTo("user",ParseUser.getCurrentUser());
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e != null) {
                    Log.d("Count", "CountError");
                    e.printStackTrace();
                    return;
                }
                tvPostCount.setText(String.valueOf(count));
            }
        });

        Glide.with(getActivity())
                .load(ParseUser.getCurrentUser().getParseFile("avatar").getUrl())
                .into(imageView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.account_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_action_logout :
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                ((AppCompatActivity)getActivity()).finish();
                return true;
                default:
                    return false;
        }
    }
}
