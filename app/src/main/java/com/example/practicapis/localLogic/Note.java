package com.example.practicapis.localLogic;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.time.*;


public class Note implements Parcelable, Comparable<Note> {
    private String title;
    private String body;
    private String id;
    private LocalDateTime date;
    private boolean favorite;
    private LatLng location;

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Note(){
        favorite = false;
    }

    public Note(String title, String body, String id, LocalDateTime date) {
        this.title = title;
        this.body = body;
        this.id = id;
        this.date = date;
        favorite = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Note(Parcel in) {
        title = in.readString();
        body = in.readString();
        id = in.readString();
        favorite = Boolean.valueOf(in.readString());
        String locationFromParcel = in.readString();
        if(locationFromParcel != null){
            location = convertStringToLatLng(locationFromParcel);
        }

    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
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

    public LatLng getLocation(){ return location; }

    public LocalDateTime getDate(){ return date; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(LocalDateTime date){ this.date = date; }

    public void setLocation(LatLng location){ this.location = location; }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(id);
        dest.writeString(String.valueOf(favorite));
        if(location != null){
            dest.writeString(location.toString());
        }
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

    private LatLng convertStringToLatLng(String location){
        String[] latlong =  location.split(",");
        String[] lat = latlong[0].split("\\(");
        String[] lng = latlong[1].split("\\)");

        double latitude = Double.parseDouble(lat[1]);
        double longitude = Double.parseDouble(lng[0]);
        return new LatLng(latitude, longitude);
    }
}
