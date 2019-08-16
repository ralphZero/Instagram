package com.ralph.instagram.models;

import android.icu.text.LocaleDisplayNames;
import android.util.Log;

import com.parse.CountCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_LIKECOUNT  = "likecount";
    public static final String KEY_COMMENTCOUNT = "commentcount";

    public Post() {
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String value){
        put(KEY_DESCRIPTION, value);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile value){
        put(KEY_IMAGE, value);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser value){
        put(KEY_USER, value);
    }

    public void setLikeCount(int value){put(KEY_LIKECOUNT,value);}

    public int getLikeCount(){return getInt(KEY_LIKECOUNT);}

    public void setCommentCount(int value){put(KEY_COMMENTCOUNT,value);}

    public int getCommentCount(){return getInt(KEY_COMMENTCOUNT);}

}
