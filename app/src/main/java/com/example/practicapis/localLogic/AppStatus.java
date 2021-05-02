package com.example.practicapis.localLogic;

import java.util.ArrayList;
import java.util.Collections;

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

    public void setAllNotes(ArrayList<Note> notesList){
        if(notesList != null){
            this.notesList = sortByFavourite(notesList);
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
        notesView = false;
    }

    public boolean isArchivedView(){ return archivedView; }

    public void setNotesView(){
        notesView = true;
        archivedView = false;
    }

    public boolean isStart(){ return start; }

    public void startApp(){ start = false; }

    private ArrayList<Note> sortByFavourite(ArrayList<Note> notes){
        ArrayList<Note> favourites = new ArrayList<>();
        ArrayList<Note> notFavourites = new ArrayList<>();
        for(Note note : notes){
            if(note.isFavorite()){
                favourites.add(note);
            }else{
                notFavourites.add(note);
            }
        }
        Collections.sort(favourites, Collections.reverseOrder());
        Collections.sort(notFavourites, Collections.reverseOrder());
        favourites.addAll(notFavourites);
        return favourites;
    }
}
