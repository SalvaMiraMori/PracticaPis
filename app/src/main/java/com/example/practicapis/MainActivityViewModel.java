package com.example.practicapis;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel implements DatabaseAdapter.vmInterface{

    private final MutableLiveData<ArrayList<Note>> mNotes;
    private final MutableLiveData<String> mToast;

    public static final String TAG = "ViewModel";

    public MainActivityViewModel(){
        mNotes = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        DatabaseAdapter da = new DatabaseAdapter(this);
        da.setListener(this);
        da.getCollection();
    }

    @Override
    public void setCollection(ArrayList<Note> ac) {
        mNotes.setValue(ac);
    }

    @Override
    public void setToast(String s) {
        mToast.setValue(s);
    }

    public MutableLiveData<ArrayList<Note>> getNotes(){
        return mNotes;
    }
    public Note getNote(int idx){
        return mNotes.getValue().get(idx);
    }

    public void addNote(Note note){
        mNotes.getValue().add(note);
        mNotes.setValue(mNotes.getValue());
        //note.saveNoteToDb();
    }
}
