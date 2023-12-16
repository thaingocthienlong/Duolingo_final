package com.app.duolingo.models;

import java.util.List;

public class Topic {
    private String id;
    private String title;
    private String description;
    private List<WordTopic> wordList;

    public Topic(String id, String title, String description, List<WordTopic> wordList) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.wordList = wordList;
    }

    public Topic() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<WordTopic> getWordList() {
        return wordList;
    }

    public void setWordList(List<WordTopic> wordList) {
        this.wordList = wordList;
    }
}
