package com.ralph.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;


public class AccountActivity extends AppCompatActivity {

    ImageView ivAccountImg;
    EditText etAccountName,etAccountUsername,etAccountBio;
    TextView tvAccountEmail;
    File photoFile;
    ParseUser user;
    Boolean hasTakenAnImage = false;
    ProgressBar progressBar;

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 444;
    private static final String APP_TAG = "SignupActivity";
    static final String photoFileName = "avatar.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar8);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("Edit account");

        ivAccountImg = findViewById(R.id.ivAccountImg);
        etAccountName = findViewById(R.id.etAccountName);
        etAccountUsername = findViewById(R.id.etAccountUsername);
        etAccountBio = findViewById(R.id.etAccountBio);
        tvAccountEmail = findViewById(R.id.tvAccountEmail);
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        user = ParseUser.getCurrentUser();

        initBinding();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ic_action_save) {
            saveAccountData();
            return true;
        }
        return false;
    }

    private void saveAccountData() {
        //todo : save data to parse
        progressBar.setVisibility(View.VISIBLE);

        ParseUser object = ParseUser.getCurrentUser();
        object.put("username",etAccountUsername.getText().toString());
        object.put("name",etAccountName.getText().toString());
        object.put("bio",etAccountBio.getText().toString());
        if(hasTakenAnImage){
            object.put("avatar",new ParseFile(photoFile));
        }
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                    return;
                }
                //progress
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_item,menu);
        return true;
    }

    private void initBinding() {
        Glide.with(this)
                .load(user.getParseFile("avatar").getUrl())
                .into(ivAccountImg);

        etAccountName.setText(user.getString("name"));
        etAccountUsername.setText(user.getUsername());
        etAccountBio.setText(user.getString("bio"));
        tvAccountEmail.setText(user.getEmail());

        ivAccountImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

    }

    private void openCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(AccountActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                hasTakenAnImage = true;
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivAccountImg.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                hasTakenAnImage = false;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
