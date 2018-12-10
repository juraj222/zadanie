package com.mv.jura.firebase_example;

import android.annotation.SuppressLint;

import com.google.android.exoplayer2.ExoPlayer;

import java.io.Serializable;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Item implements Serializable {
    String userId;
    String name;
    String date;
    Date dateObject;
    String registrationDate;
    Date registrationDateObject;
    String postCount;
    String imageUrl;
    String videoUrl;
    boolean isProfile;

    public Item() {
    }

    public Item(String userId) {
        this.userId = userId;
    }

    public Item(String name, String registrationDate, String date, Date dateObject, String postCount, String imageUrl, String videoUrl, boolean isProfile) {
        this.name = name;
        this.date = date;
        this.dateObject = dateObject;
        this.registrationDate = registrationDate;
        this.postCount = postCount;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.isProfile = isProfile;
    }

    public Date getRegistrationDateObject() {
        return registrationDateObject;
    }

    public void setRegistrationDateObject(Date registrationDateObject) {
        this.registrationDateObject = registrationDateObject;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
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
    @SuppressLint("NewApi")
    public String getRegistrationDateFormated() {
        SimpleDateFormat format = new SimpleDateFormat("d MMMM 'o' HH:mm:ss", Locale.forLanguageTag("sk-SK"));
        //Date dateValue = input.parse(startTime);
        return format.format(getRegistrationDateObject());
    }
    @SuppressLint("NewApi")
    public String getDateFormated() {
        SimpleDateFormat format = new SimpleDateFormat("d MMMM 'o' HH:mm:ss", Locale.forLanguageTag("sk-SK"));
        //Date dateValue = input.parse(startTime);
        return format.format(getDateObject());
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
