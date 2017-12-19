package com.example.my.sapproject;

public class User {
    public String photo;
    public String uid;
    public String txt;

    public User(){

    }

    public User(String uid, String photo, String txt){
        this.uid = uid;
        this.photo = photo;
        this.txt = txt;
    }
}
