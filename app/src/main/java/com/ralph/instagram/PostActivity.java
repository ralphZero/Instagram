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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ivShow = findViewById(R.id.ivShow);
        etDesc = findViewById(R.id.etDescPost);
        btnPost = findViewById(R.id.btnPost);

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
                if(e!=null){
                    Log.d("ErrorSavePost","error while saving post.");
                    e.printStackTrace();
                    return;
                }
                //todo : intent to main activity
                Toast.makeText(PostActivity.this,"Success",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
