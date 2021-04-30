package com.example.practicapis;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AppStatus {
    private Boolean start;
    private static AppStatus instance;

    private ArrayList<Note> archivedNotes = new ArrayList<>();
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

    public void editArchiveNote(Note note, int position){
        archivedNotes.remove(position);
        archivedNotes.add(0, note);
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

    public Note getNoteArchivedByPosition(int position){
        return archivedNotes.get(position);
    }

    //TODO create metodes to archive and unarchive notes
    public void setArchivedNotes(ArrayList<Note> archivedNotes){ this.archivedNotes = archivedNotes; }

    public ArrayList<Note> getArchivedNotes(){return archivedNotes;}

    public void archiveNote(Note note) {
        this.notesList.remove(note);
        this.archivedNotes.add(0, note);
    }
    public void unarchiveNote(int position) {
        this.archivedNotes.remove(position);
    }
    public boolean isNoteArchived(Note note) {
        for (int i = 0; i < archivedNotes.size(); i++) {
            if (archivedNotes.get(i).equals(note))
                return true;
        }
        return false;
    }
}
