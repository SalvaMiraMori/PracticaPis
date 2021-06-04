package com.example.practicapis.viewModel;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.practicapis.database.DatabaseAdapter;
import com.example.practicapis.localLogic.Note;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class NoteActivityViewModel extends ViewModel implements DatabaseAdapter.NotaActivityInterface{
    private final DatabaseAdapter adapter;
    private MutableLiveData<ArrayList<Bitmap>> mBitmapList;
    private ArrayList<Bitmap> bitmapList;

    public static final String TAG = "NoteActivityViewModel";

    public NoteActivityViewModel(){
        mBitmapList = new MutableLiveData<>();
        bitmapList = new ArrayList<>();
        mBitmapList.setValue(bitmapList);
        adapter = DatabaseAdapter.databaseAdapter;
        adapter.setNotaActivityInterface(this);
    }

    public void addNote(Note note){
        Log.d(TAG, "Adding note.");
        adapter.saveNote(note);
    }

    public void archiveNote(Note note){
        Log.d(TAG, "Archiving note.");
        adapter.archiveNote(note);
    }

    public void deleteNote(Note note){
        Log.d(TAG, "Deleting note.");
        adapter.deleteNote(note);
    }

    public void editNote(Note note){
        Log.d(TAG, "Editing note.");
        adapter.editNote(note);
    }

    public void deleteArchivedNote(Note note) {
        Log.d(TAG, "Deleting archived note.");
        adapter.deleteArchivedNote(note);
    }

    public void saveToDB(ByteArrayOutputStream baos, String fileID){
        adapter.saveImages(baos, fileID);
    }

    public void recoverFile(ArrayList<String> fileIDList){
        adapter.recoverImage(fileIDList);
    }

    @Override
    public void setImageBitmapList(ArrayList<Bitmap> bitmapList) {
        this.mBitmapList.setValue(bitmapList);
    }

    @Override
    public void addImageBitmap(Bitmap bitmap) {
        Log.d(TAG, "Adding bitmap image");
        bitmapList.add(bitmap);
        this.mBitmapList.setValue(bitmapList);
    }

    public MutableLiveData<ArrayList<Bitmap>> getBitmapImageList(){ return mBitmapList; }
}
