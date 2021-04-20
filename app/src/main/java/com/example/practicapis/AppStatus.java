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

    public void addNotaToList(String title, String body){
        id++;
        listWithID.add(0, new Note(title, body));


        /*for(Note note1 : listWithID){
            System.out.println("Elemento: "+note1.getId()+", "+note1.getTitle()+", "+note1.getBody());
        }*/


    }

    public ArrayList<Note> getAllNotes(){
        return listWithID;
    }

    public Note getNoteByPosition(int position){
        return listWithID.get(position);
    }


}
