package com.example.practicapis;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


public class Note implements Parcelable {
    private String title;
    private String body;
    private String id;

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Note(){}

    public Note(String title, String body, String id) {
        this.title = title;
        this.body = body;
        this.id = id;
    }

    protected Note(Parcel in) {
        title = in.readString();
        body = in.readString();
        id = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getId(){ return id; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(id);
    }

}
