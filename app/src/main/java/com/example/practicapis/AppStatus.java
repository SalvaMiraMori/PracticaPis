package com.example.practicapis;

import java.util.ArrayList;

public class AppStatus {
    private Boolean start;
    private static AppStatus instance;
    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayList<Note> archiveNotes = new ArrayList<>();

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
        this.notes.remove(position);
        this.notes.add(0, note);
    }

    public void deleteNote(int position){
        notes.remove(position);
    }

    public void addNote(Note note){
        this.notes.add(0, note);
    }

    public ArrayList<Note> getAllNotes(){
        return notes;
    }

    public Note getNoteByPosition(int position){
        return notes.get(position);
    }

    //TODO create metodes to archive and unarchive notes
    public ArrayList<Note> getArchiveNotes(){return archiveNotes;}
    public void archiveNote(Note note) {
        this.notes.remove(note);
        this.archiveNotes.add(0, note);
    }
    public void unarchiveNote(Note note) {
        this.archiveNotes.remove(note);
        this.notes.add(note);
    }
}
