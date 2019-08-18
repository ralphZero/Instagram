package com.ralph.instagram.fragment;

import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.ralph.instagram.EndlessRecyclerViewScrollListener;
import com.ralph.instagram.R;
import com.ralph.instagram.adapters.HomeAdapter;
import com.ralph.instagram.models.Post;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static int SKIP_VALUE;
    RecyclerView rvInsta;
    Toolbar toolbar;
    HomeAdapter adapter;
    SwipeRefreshLayout refreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;
    List<Post> list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container,false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        toolbar =view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        list = new ArrayList<Post>();
        rvInsta = view.findViewById(R.id.rvInsta);
        adapter = new HomeAdapter(getActivity(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMorePost();
            }
        };

        rvInsta.setOnScrollListener(scrollListener);
        rvInsta.setLayoutManager(layoutManager);
        rvInsta.setAdapter(adapter);

        fetchAllPosts();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAllPosts();
            }
        });
    }

    private void loadMorePost() {
        ParseQuery<Post> postParseQuery = new ParseQuery<Post>(Post.class);
        postParseQuery.include(Post.KEY_USER);
        postParseQuery.setLimit(20);
        postParseQuery.setSkip(SKIP_VALUE);
        postParseQuery.orderByDescending("createdAt");
        postParseQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                refreshLayout.setRefreshing(false);
                if(e != null){
                    Log.e("Error fetch","Error while fetching posts.");
                    e.printStackTrace();
                    return;
                }

                for(int i=0;i<posts.size();i++){
                    adapter.addToList(posts.get(i));
                }
                SKIP_VALUE += posts.size();
                scrollListener.resetState();
            }
        });
    }

    private void fetchAllPosts() {
        ParseQuery<Post> postParseQuery = new ParseQuery<Post>(Post.class);
        postParseQuery.include(Post.KEY_USER);
        postParseQuery.setLimit(20);
        postParseQuery.orderByDescending("createdAt");
        postParseQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                refreshLayout.setRefreshing(false);
                if(e != null){
                    Log.e("Error fetch","Error while fetching posts.");
                    e.printStackTrace();
                    return;
                }
                adapter.addAllToList(posts);
                SKIP_VALUE = posts.size();
            }
        });
    }


}
