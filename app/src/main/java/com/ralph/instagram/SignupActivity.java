package com.ralph.instagram;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;


public class SignupActivity extends AppCompatActivity {

    private static final String APP_TAG = "SignupActivity";
    EditText name,username,email,password,confpassword;
    ImageView ivAvatar;
    Button btnSignup;
    File photoFile;
    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1234;
    static final String photoFileName = "avatar.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Sign Up");

        name = findViewById(R.id.etSuName);
        username = findViewById(R.id.etSuUsername);
        email = findViewById(R.id.etSuEmail);
        password = findViewById(R.id.etSuPassword);
        confpassword = findViewById(R.id.etSuConfPasword);
        ivAvatar = findViewById(R.id.ivSignUpPhoto);
        btnSignup = findViewById(R.id.btnSignup);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mName = name.getText().toString();
                String mUsername = username.getText().toString();
                String mEmail = email.getText().toString();
                String mPassword = password.getText().toString();
                if(photoFile == null){
                    Snackbar.make(view,"Please add image",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(!mPassword.contentEquals(confpassword.getText().toString())){
                    Snackbar.make(view,"Password not matching.",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(mName.isEmpty() || mEmail.isEmpty() || mUsername.isEmpty() || mPassword.isEmpty()){
                    Snackbar.make(view,"All fields are mandatory",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                signupParse(mName,mUsername,mEmail,mPassword,photoFile);
            }
        });

    }

    private void signupParse(final String mName, final String mUsername, final String mEmail, final String mPassword, File photoFile) {

        final ParseFile file = new ParseFile(photoFile);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                     Log.e(APP_TAG,"Error img"+e.getMessage());
                     e.printStackTrace();
                     return;
                }
                ParseUser user = new ParseUser();
                user.setUsername(mUsername);
                user.setEmail(mEmail);
                user.setPassword(mPassword);
                user.put("name",mName);
                user.put("handle",mUsername);
                user.put("avatar",file);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                            startActivity(intent);
                            LoginActivity.usernameCookie = mUsername;
                            finish();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            e.printStackTrace();
                            Toast.makeText(SignupActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
        Uri fileProvider = FileProvider.getUriForFile(SignupActivity.this, "com.codepath.fileprovider", photoFile);
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
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivAvatar.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
