package com.example.practicapis;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsStatus {

    private static MapsStatus instance;
    private ArrayList<LatLng> allVisitedLocations;
    private HashMap<Integer, ArrayList<LatLng>> allPreviousLocationByNote = new HashMap<Integer, ArrayList<LatLng>>();

    public static MapsStatus getInstance(){
        if(instance == null){
            instance = new MapsStatus();
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addLocationOfNote(int position, LatLng ubi){
        if(allPreviousLocationByNote.containsKey(position)){
            allVisitedLocations = allPreviousLocationByNote.get(position);
            allVisitedLocations.add(ubi);
            allPreviousLocationByNote.replace(position, allVisitedLocations);
        } else {
            allVisitedLocations = new ArrayList<LatLng>();
            allVisitedLocations.add(ubi);
            allPreviousLocationByNote.put(position, allVisitedLocations);
        }
    }

    public ArrayList<LatLng> getAllPreviouslyVisitedLocations(int position){
        return allPreviousLocationByNote.get(position);
    }

}
