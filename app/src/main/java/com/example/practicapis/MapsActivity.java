package com.example.practicapis;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.security.Permissions;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        OnMyLocationClickListener, OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    private int LOCATION_PERMISSION_REQUEST = 1;
    private boolean enabled = false;
    private SearchView searchView;
    private int position;
    private MapsStatus mapsStatus;
    private FloatingActionButton saveLocationBtn;
    private LatLng lastLocation;

    private void enableMyLocation(){
        if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        } else{
            String [] array = {ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, array, LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_PERMISSION_REQUEST){
            for(int i = 0; i<grantResults.length && enabled == false; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    enabled = true;
                }
            }
            if(enabled == true){
                enableMyLocation();
            } else{
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        saveLocationBtn = findViewById(R.id.saveLocationBtn);
        saveLocationBtn.setEnabled(false);

        saveLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocation();
            }
        });




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        int status= GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if(status == ConnectionResult.SUCCESS){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            //setAllPreviousLocations();
            searchView = findViewById(R.id.search_btn);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String location = searchView.getQuery().toString();
                    List<Address> addressList = null;

                    if(location != null || !location.equals("")){
                        Geocoder geocoder = new Geocoder(MapsActivity.this);
                        try{
                            addressList = geocoder.getFromLocationName(location, 1);
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                        //Mirem si hi existeix l'adreca
                        if(addressList != null && addressList.size() != 0) {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            saveLocationBtn.setEnabled(true);
                            lastLocation = latLng;
                        }else{
                            Toast.makeText(getApplicationContext(), "Address doesn't exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            mapFragment.getMapAsync(this);
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity)getApplicationContext(), 10);
            dialog.show();
        }
        //getBundlesData();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
        getBundlesData();
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_SHORT).show();
        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        String address = getAddress(lastLocation.latitude, lastLocation.longitude);
        mMap.addMarker(new MarkerOptions().position(lastLocation).title(address));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 10));
        saveLocationBtn.setEnabled(true);
    }

    public void setAllPreviousLocations(){
        String address;
        for(LatLng latLng : mapsStatus.getAllPreviouslyVisitedLocations(position)){
            address = getAddress(latLng.latitude, latLng.longitude);
            mMap.addMarker(new MarkerOptions().position(latLng).title(address));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }
    }

    public void getBundlesData(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            lastLocation = (LatLng) bundle.get("location");
            String address = getAddress(lastLocation.latitude, lastLocation.longitude);
            mMap.addMarker(new MarkerOptions().position(lastLocation).title(address));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 10));
        }
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            return add;
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void saveLocation(){
        //TODO: save location
        Intent intent = new Intent();
        intent.putExtra("location", lastLocation.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    private LatLng convertStringToLatLng(String location){
        String[] latlong =  location.split(",");
        String[] lat = latlong[0].split("\\(");
        String[] lng = latlong[1].split("\\)");

        double latitude = Double.parseDouble(lat[1]);
        double longitude = Double.parseDouble(lng[0]);
        return new LatLng(latitude, longitude);
    }
}