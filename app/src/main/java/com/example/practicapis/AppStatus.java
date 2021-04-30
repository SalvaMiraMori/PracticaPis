package com.example.practicapis;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AppStatus {
    private Boolean start;
    private static AppStatus instance;

    private ArrayList<Note> archivedNotes = new ArrayList<>();
    private ArrayList<Note> notesList = new ArrayList<>();
    private boolean archivedView;
    private boolean notesView;

    public AppStatus(){
        start = true;
    }

    public static AppStatus getInstance(){
        if(instance == null){
            instance = new AppStatus();
        }
        return instance;
    }

    public ArrayList<Note> getAllNotes(){
        return notesList;
    }

    public void setAllNotes(ArrayList<Note> notesList){ this.notesList = notesList; }

    public Note getNoteByPosition(int position){
      return notesList.get(position);
    }

    public Note getNoteArchivedByPosition(int position){
        return archivedNotes.get(position);
    }

    public void setArchivedNotes(ArrayList<Note> archivedNotes){ this.archivedNotes = archivedNotes; }

    public ArrayList<Note> getArchivedNotes(){return archivedNotes;}

    public void setArchivedView(){
        archivedView = true;
        notesView = false;
    }

    public boolean isArchivedView(){ return archivedView; }

    public void setNotesView(){
        notesView = true;
        archivedView = false;
    }
}
