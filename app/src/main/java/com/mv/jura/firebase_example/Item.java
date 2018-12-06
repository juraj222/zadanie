package com.mv.jura.firebase_example;

import com.google.android.exoplayer2.ExoPlayer;

import java.net.URI;

public class Item {
    String userId;
    String name;
    String date;
    String registrationDate;
    String postCount;
    String imageUrl;
    String videoUrl;
    boolean isProfile;

    public Item() {
    }

    public Item(String userId) {
        this.userId = userId;
    }

    public Item(String name, String registrationDate, String date, String postCount, String imageUrl, String videoUrl, boolean isProfile) {
        this.name = name;
        this.date = date;
        this.registrationDate = registrationDate;
        this.postCount = postCount;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.isProfile = isProfile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPostCount() {
        return postCount;
    }

    public void setPostCount(String postCount) {
        this.postCount = postCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean isProfile() {
        return isProfile;
    }

    public void setProfile(boolean profile) {
        isProfile = profile;
    }
}
