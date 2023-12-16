package com.app.duolingo.models;

import com.google.firebase.database.Exclude;

import java.util.Objects;

public class WordTopic {
    private String id;
    private String english;
    private String meaning;
    private String pronounce;

    public WordTopic(String id, String english, String meaning, String pronounce) {
        this.id = id;
        this.english = english;
        this.meaning = meaning;
        this.pronounce = pronounce;
    }

    public WordTopic() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }
}
