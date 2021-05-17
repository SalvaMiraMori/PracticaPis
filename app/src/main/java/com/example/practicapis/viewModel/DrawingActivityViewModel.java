package com.example.practicapis.viewModel;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.practicapis.database.DatabaseAdapter;

import java.io.ByteArrayOutputStream;

public class DrawingActivityViewModel implements DatabaseAdapter.DrawingInterface{
    DatabaseAdapter adapter;
    Bitmap bitmap;
    MutableLiveData<Bitmap> mBitmap;

    public DrawingActivityViewModel(){
        adapter = new DatabaseAdapter();
        adapter.setDrawingInterfaceListener(this);
        mBitmap = new MutableLiveData<>();
    }

    public void saveToDb(ByteArrayOutputStream baos, String drawingId){
        adapter.saveDrawing(baos, drawingId);
    }

    public void recoverDrawing(String drawingId){
        adapter.recoverDrawing(drawingId);
    }

    @Override
    public void setDrawingBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        Log.d("setbitmapDAVM", bitmap.toString());
        mBitmap.setValue(bitmap);
    }

    public MutableLiveData<Bitmap> getBitmap(){
        Log.d("getbitmapDAVM", mBitmap.toString());
        return mBitmap;
    }
}
