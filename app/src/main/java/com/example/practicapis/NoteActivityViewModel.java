package com.example.practicapis;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class NoteActivityViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Note>> mNotes;
    private final MutableLiveData<String> mToast;
    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    public static final String TAG = "ViewModel";

    public NoteActivityViewModel(){
        mNotes = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
    }

    public void addNote(Note note){
        Log.d("saveCard", "saveCard-> saveDocument");
        adapter.saveNote(note);
    }

    public void deleteNote(Note note){
        adapter.deleteNote(note);
    }

    public void editNote(Note note){
        adapter.editNote(note);
    }

    public LiveData<ArrayList<Note>> getNotes(){
        return mNotes;
    }
}
