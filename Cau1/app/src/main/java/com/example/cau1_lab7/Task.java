package com.example.cau1_lab7;

public class Task {
    private long id;
    private String title;
    private String content;
    private String datetime;

    public Task(long id, String title, String content, String datetime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.datetime = datetime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        if (datetime == null || datetime.trim().isEmpty()) {
            return title + (content.isEmpty() ? "" : " - " + content);
        }
        return title + " - " + datetime;
    }
}
