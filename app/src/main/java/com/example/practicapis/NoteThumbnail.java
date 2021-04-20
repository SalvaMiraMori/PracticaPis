package com.example.practicapis;

public class NoteThumbnail {
    private int id;
    private String title;
    private String body;

    public NoteThumbnail(String title, String body) {
        this.title = title;
        this.body = body;
    }

    /*public NoteThumbnail(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }*/

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
