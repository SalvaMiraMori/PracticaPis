package com.example.practicapis.localLogic;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class Image implements Parcelable, Comparable<Image>{

    private Bitmap imatge;

    public Image(Bitmap imatge){
        this.imatge = imatge;
    }

    public void setImatge(Bitmap imatge){
        this.imatge = imatge;
    }

    public Bitmap getImatge(){
        return imatge;
    }

    protected Image(Parcel in) {
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int compareTo(Image o) {
        return 0;
    }
}
