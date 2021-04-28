package com.example.practicapis;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class Note implements Parcelable {
    private String title;
    private String body;
    private boolean favorite;

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
    }

    protected Note(Parcel in) {
        title = in.readString();
        body = in.readString();
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
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Note note = (Note) obj;
        return this.body.equals(note.body) && this.title.equals(note.title);
    }
}
