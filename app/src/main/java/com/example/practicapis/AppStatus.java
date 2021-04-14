package com.example.practicapis;

public class AppStatus {
    private Boolean start;
    private static AppStatus instance;

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
}
