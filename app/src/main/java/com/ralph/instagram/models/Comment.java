package com.ralph.instagram.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_POST = "post";

    public void setUser(ParseUser value){put(KEY_USER,value);}
    public ParseUser getUser(){return getParseUser(KEY_USER);}
    public void setComment(String value){put(KEY_COMMENT,value);}
    public String getComment(){return getString(KEY_COMMENT);}
    public void setPost(ParseObject value){put(KEY_POST,value);}
}
