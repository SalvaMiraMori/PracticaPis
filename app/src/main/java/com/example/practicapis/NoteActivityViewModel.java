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
        //mNotes.setValue(adapter.getCollection());
        mToast = new MutableLiveData<>();
        /*DatabaseAdapter da = new DatabaseAdapter(this);
        da.getCollection();*/
    }

    public void addNote(Note note){
        //mNotes.getValue().add(note);
        //mNotes.setValue(mNotes.getValue());
        //note.saveNoteToDb();
        Log.d("saveCard", "saveCard-> saveDocument");
        adapter.saveNote(note);
    }
}
