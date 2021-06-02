package com.example.practicapis.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.practicapis.database.DatabaseAdapter;
import com.example.practicapis.localLogic.Note;

import java.util.ArrayList;

public class MainArchiveViewModel extends ViewModel implements DatabaseAdapter.MainInterface {

    private final MutableLiveData<ArrayList<Note>> mNotes;
    private final MutableLiveData<ArrayList<Note>> mArchivedNotes;
    private final MutableLiveData<String> mToast;

    public static final String TAG = "ViewModel";

    public MainArchiveViewModel(){
        mNotes = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        mArchivedNotes = new MutableLiveData<>();
        DatabaseAdapter da = new DatabaseAdapter(this);
        da.getNotes();
        da.getArchivedNotes();
    }

    @Override
    public void setNotes(ArrayList<Note> notes) {
        //IGNORE
    }

    @Override
    public void setArchivedNotes(ArrayList<Note> archivedNotes) { mArchivedNotes.setValue(archivedNotes); }

    @Override
    public void setToast(String s) {
        mToast.setValue(s);
    }

    public MutableLiveData<ArrayList<Note>> getNotes(){
        //IGNORE
        return null;
    }
    public MutableLiveData<ArrayList<Note>> getArchivedNotes(){ return mArchivedNotes; }
}