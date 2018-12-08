package com.mv.jura.firebase_example.adapters;

public class LoadedChecker {
    Boolean profileLoaded = false;
    Boolean postsLoaded = false;

    public Boolean getProfileLoaded() {
        return profileLoaded;
    }

    public void setProfileLoaded(Boolean profileLoaded) {
        this.profileLoaded = profileLoaded;
    }

    public Boolean getPostsLoaded() {
        return postsLoaded;
    }

    public void setPostsLoaded(Boolean postsLoaded) {
        this.postsLoaded = postsLoaded;
    }
}
