package com.example.practicapis.viewModel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.practicapis.database.DatabaseAdapter;
import com.example.practicapis.localLogic.Note;

import java.io.ByteArrayOutputStream;

public class NoteActivityViewModel extends ViewModel {
    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    public static final String TAG = "NoteActivityViewModel";

    public NoteActivityViewModel(){
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

    public void recoverFile(String fileID){
        adapter.recoverImage(fileID);
    }
}
