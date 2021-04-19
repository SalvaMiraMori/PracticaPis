package com.example.practicapis;

public class NoteThumbnail {
    private long id;
    private String title;
    private String body;

    public NoteThumbnail(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public NoteThumbnail(long id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
