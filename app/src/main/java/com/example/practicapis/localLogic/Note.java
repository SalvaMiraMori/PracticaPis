package com.example.practicapis.localLogic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.widget.ImageView;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class Note implements Parcelable, Comparable<Note> {
    private String title;
    private String body;
    private String id;
    private LocalDateTime date;
    private boolean favorite;
    private LatLng location;
    private String drawingId;
    private ArrayList<Image> fileList;
    private String color;
    private ArrayList<String> tags;

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
        tags = new ArrayList<>();
        color = "#F3C22E";
    }

    public Note(){
        tags = new ArrayList<>();
        favorite = false;
        color = "#F3C22E";
    }

    public Note(String title, String body, String id, LocalDateTime date) {
        this.title = title;
        this.body = body;
        this.id = id;
        this.date = date;
        favorite = false;
        tags = new ArrayList<>();
        color = "#F3C22E";
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Note(Parcel in) {
        title = in.readString();
        body = in.readString();
        id = in.readString();
        favorite = Boolean.valueOf(in.readString());
        String locationFromParcel = in.readString();
        if(!locationFromParcel.equals("null")){
            location = convertStringToLatLng(locationFromParcel);
        }
        String drawingIdFromParcel = in.readString();
        if(!drawingIdFromParcel.equals("null")){
            drawingId = drawingIdFromParcel;
        }
        color = in.readString();
        tags = new ArrayList(Arrays.asList(in.readArray(getClass().getClassLoader())));

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

    public String getDrawingId(){ return drawingId; }


    public ArrayList<Image> getFileList(){ return fileList; }

    public ArrayList<String> getTags() { return tags; }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public void setTags(ArrayList<String> tags) { this.tags = tags; }

    public void setDrawingId(String drawingId){ this.drawingId = drawingId; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(LocalDateTime date){ this.date = date; }

    public void setLocation(LatLng location){ this.location = location; }

    public void setFileList(ArrayList<Image> fileList){
        if(fileList != null) {
            this.fileList = fileList;
        }
    }

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
        }else{
            dest.writeString("null");
        }

        if(drawingId != null){
            dest.writeString(drawingId);
        }else{
            dest.writeString("null");
        }
        dest.writeString(color);
        try{
            dest.writeArray(tags.toArray());
        }catch(Exception e){}

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


    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
      
    }

    public void addTag(String tag){
        tags.add(tag);
    }

    public boolean deleteTag(String tag){
        if(tags.contains(tag)){
            tags.remove(tag);
            return true;
        }
        return false;
    }

    public boolean containsTag(String tag){
        if(tags == null){
            Log.d("Note", "tags null");
            return false;
        }
        for(String auxTag : tags){
            if(auxTag.equals(tag)){
                return true;
            }
        }
        return false;
    }
}
