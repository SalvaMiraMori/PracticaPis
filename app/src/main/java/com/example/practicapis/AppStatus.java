package com.example.practicapis;

import java.util.ArrayList;

public class AppStatus {
    private Boolean start;
    private static AppStatus instance;
    private ArrayList<Note> notesList = new ArrayList<>();

    private int id = 0;

    public AppStatus(){
        start = true;
    }

    public static AppStatus getInstance(){
        if(instance == null){
            instance = new AppStatus();
        }
        return instance;
    }

    public void appStarted(){
        start = false;
    }

    public boolean checkStarted(){ return start; }


    public void editNote(Note note, int position){
        notesList.remove(position);
        notesList.add(0, note);
    }

    public void deleteNote(int position){
        notesList.remove(position);
    }

    public void addNote(Note note){
        notesList.add(0, note);
    }

    public ArrayList<Note> getAllNotes(){
        return notesList;
    }

    public void setAllNotes(ArrayList<Note> notesList){ this.notesList = notesList; }

    public Note getNoteByPosition(int position){
        return notesList.get(position);
    }

}
