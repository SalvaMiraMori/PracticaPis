package com.example.practicapis;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppStatus {
    private Boolean start;
    private static AppStatus instance;
    private ArrayList<NoteThumbnail> listWithID= new ArrayList<>();
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
        listWithID.add(0, new NoteThumbnail(title, body));


        /*for(NoteThumbnail note1 : listWithID){
            System.out.println("Elemento: "+note1.getId()+", "+note1.getTitle()+", "+note1.getBody());
        }*/


    }

    public ArrayList<NoteThumbnail> getAllNotes(){
        return listWithID;
    }

    public NoteThumbnail getNoteByPosition(int position){
        return listWithID.get(position);
    }


}
