package com.example.signupapp.ui.gallery;

public class Upload {
    private String mName;
    private String mImageUrl;/*
    private String mImageLocation;*/

    public Upload(String trim, String s){
        //empty constructor needed
        this.mName = trim;
        this.mImageUrl = s;
    }
    public Upload(){

    }
    public Upload(String name, String imageUrl, String imageLocation){
        if(name.trim().equals("")){
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;/*
        mImageLocation = imageLocation;*/
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }

    /*public String getImageLocation() {
        return mImageLocation;
    }

    public void setImageLocation(String imageLocation) {
        mImageLocation = imageLocation;
    }*/
}
