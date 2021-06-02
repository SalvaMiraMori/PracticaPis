package com.example.practicapis.localLogic;

import java.util.ArrayList;
import java.util.Collections;

public class AppStatus {
    private Boolean start;
    private static AppStatus instance;

    private ArrayList<Note> archivedNotes = new ArrayList<>();
    private ArrayList<Note> notesList = new ArrayList<>();
    //private ArrayList<Note>
    private boolean archivedView;

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

    public void setAllNotes(ArrayList<Note> notesList){
        if(notesList != null){
            this.notesList = notesList;
        }
    }

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
    }

    public boolean isArchivedView(){ return archivedView; }

    public void setNotesView(){
        archivedView = false;
    }

    public boolean isStart(){ return start; }

    public void startApp(){ start = false; }
}
