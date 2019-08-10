package com.ralph.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    AnimationDrawable animationDrawable;
    RelativeLayout relativeLayout;
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    TextView tvSignup;

    static String TAG = "LoginActivity";
    public static String usernameCookie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            goToActivity();
        } else {
            // show the signup or login screen
            setAnimation();
            etUsername = (EditText) findViewById(R.id.etUsername);
            etUsername.setText(usernameCookie);
            etPassword = (EditText) findViewById(R.id.etPassword);
            tvSignup = findViewById(R.id.tvMee);
            btnLogin = (Button) findViewById(R.id.btnLogin2);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnLogin.setClickable(false);
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    if(!username.contentEquals("") && !password.contentEquals("")){
                        login(username,password);
                    }
                    else{
                        if(username.contentEquals("") && !password.contentEquals("")){
                            btnLogin.setClickable(true);
                            Snackbar.make(view,"Please enter a username.",Snackbar.LENGTH_LONG).show();
                        }else if (password.contentEquals("") && !username.contentEquals("")){
                            btnLogin.setClickable(true);
                            Snackbar.make(view,"Please enter a password.",Snackbar.LENGTH_LONG).show();
                        }else if (username.contentEquals("") && password.contentEquals("")){
                            btnLogin.setClickable(true);
                            Snackbar.make(view,"Fields are required.",Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            });
            tvSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                    startActivity(intent);
                }
            });
        }


    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    btnLogin.setClickable(true);
                    Log.d(TAG,"Something went wrong");
                    e.printStackTrace();
                    return;
                }
                goToActivity();
            }
        });
    }

    private void goToActivity() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        //Todo: save id persistence
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAnimation();
    }

    private void setAnimation() {
        relativeLayout = (RelativeLayout) findViewById(R.id.lay);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
    }


}
