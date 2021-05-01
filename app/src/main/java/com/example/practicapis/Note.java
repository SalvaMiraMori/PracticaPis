package com.example.practicapis;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
    
import java.time.*;


public class Note implements Parcelable, Comparable<Note> {
    private String title;
    private String body;
    private String id;
    private LocalDateTime date;
    private boolean favorite;

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

    public Note(String title, String body, String id, LocalDateTime date) {
        this.title = title;
        this.body = body;
        this.id = id;
        this.date = date;
    }

    protected Note(Parcel in) {
        title = in.readString();
        body = in.readString();
        id = in.readString();
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
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

    public LocalDateTime getDate(){ return date; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(LocalDateTime date){ this.date = date; }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int compareTo(Note note) {
        return this.getDate().compareTo(note.getDate());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Note note = (Note) obj;
        return this.body.equals(note.body) && this.title.equals(note.title);
    }
}
