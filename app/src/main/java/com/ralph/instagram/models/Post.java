package com.ralph.instagram.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_LIKED = "liked";
    public static final String KEY_LIKE  = "like";
    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String value){
        put(KEY_DESCRIPTION, value);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setLiked(Boolean value){put(KEY_LIKED,value);}

    public Boolean getLiked(){return getBoolean(KEY_LIKED);}

    public void setImage(ParseFile value){
        put(KEY_IMAGE, value);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser value){
        put(KEY_USER, value);
    }
}
