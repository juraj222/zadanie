package com.mv.jura.firebase_example;

import java.net.URI;

public class Item {
    String name;
    String date;
    String registrationDate;
    String postCount;
    URI imageUrl;
    URI videoUrl;
    boolean isProfile;

    public Item() {
    }

    public Item(String name, String registrationDate, String date,  String postCount, URI imageUrl, URI videoUrl, boolean isProfile) {
        this.name = name;
        this.date = date;
        this.registrationDate = registrationDate;
        this.postCount = postCount;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.isProfile = isProfile;
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

    public URI getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URI imageUrl) {
        this.imageUrl = imageUrl;
    }

    public URI getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(URI videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean isProfile() {
        return isProfile;
    }

    public void setProfile(boolean profile) {
        isProfile = profile;
    }
}
