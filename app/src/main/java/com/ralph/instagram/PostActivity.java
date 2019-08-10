package com.ralph.instagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ralph.instagram.models.Post;

import java.io.File;
import java.io.FileOutputStream;

public class PostActivity extends AppCompatActivity {

    byte[] array = null;
    ImageView ivShow;
    EditText etDesc;
    Button btnPost;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ivShow = findViewById(R.id.ivShow);
        etDesc = findViewById(R.id.etDescPost);
        btnPost = findViewById(R.id.btnPost);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        array = getIntent().getByteArrayExtra("img");

        final File savedPhoto = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg");
        try {
            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
            outputStream.write(array);
            outputStream.close();
            // Create a bitmap
            //Bitmap result = BitmapFactory.decodeByteArray(array, 0, array.length);
            Bitmap result = BitmapFactory.decodeFile(savedPhoto.getAbsolutePath());
            ivShow.setImageBitmap(result);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo : the logic behide this would be to do a intent and post it in background in the home fragment asynchronously
                progressBar.setVisibility(View.VISIBLE);
                etDesc.setEnabled(false);
                String description = etDesc.getText().toString();
                ParseUser user = ParseUser.getCurrentUser();
                savePost(description,user,savedPhoto);
            }
        });

    }

    private void savePost(String description, ParseUser user, File savedPhoto) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(user);
        post.setImage(new ParseFile(savedPhoto));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressBar.setVisibility(View.GONE);
                etDesc.setEnabled(true);
                if(e!=null){
                    Log.d("ErrorSavePost","error while saving post.");
                    e.printStackTrace();
                    return;
                }
                Intent intent = new Intent(PostActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}
