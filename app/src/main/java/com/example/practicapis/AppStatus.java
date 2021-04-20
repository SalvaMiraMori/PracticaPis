package com.example.practicapis;

import java.util.ArrayList;

public class AppStatus {
    private Boolean start;
    private static AppStatus instance;
    private ArrayList<Note> listWithID= new ArrayList<>();
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

    // TODO: ELIMINAR
    public void addNotaToList(String title, String body){
        id++;
        listWithID.add(0, new Note(title, body));
    }

    public void editNote(Note note, int position){
        listWithID.remove(position);
        listWithID.add(0, note);
    }

    public void deleteNote(int position){
        listWithID.remove(position);
    }

    public void addNote(Note note){
        listWithID.add(0, note);
    }

    public ArrayList<Note> getAllNotes(){
        return listWithID;
    }

    public Note getNoteByPosition(int position){
        return listWithID.get(position);
    }


}
