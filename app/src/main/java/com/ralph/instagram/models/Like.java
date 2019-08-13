package com.ralph.instagram.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";

    public void setPost(ParseObject value){put(KEY_POST,value);}

    public ParseObject getPost(){return getParseObject(KEY_POST);}

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser value){
        put(KEY_USER, value);
    }
}
