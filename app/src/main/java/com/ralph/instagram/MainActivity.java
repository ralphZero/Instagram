package com.ralph.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ralph.instagram.fragment.HomeFragment;
import com.ralph.instagram.fragment.MeFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //loading the default fragment
        loadFragment(new HomeFragment());

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.ic_bnv_home);
    }

    public Boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentLayout, fragment)
                    .commit();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.ic_bnv_home:
                fragment = new HomeFragment();
                break;

            case R.id.ic_bnv_explore:
                //todo : add explore fragment
                break;

            case R.id.ic_bnv_post:
                //todo : add post fragment
                Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                startActivity(intent);
                break;

            case R.id.ic_bnv_like:
                //todo : add like fragment
                break;

            case R.id.ic_bnv_mywall:
                fragment = new MeFragment();
                break;
        }
        return loadFragment(fragment);
    }

}
