package com.app.duolingo.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Folder  {
    private String id;
    private String title;
    private String userId;

    private List<Topic> topics;




    public Folder() {
    }

    public Folder(String id, String title, String userId, List<Topic> topics) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.topics = topics;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
