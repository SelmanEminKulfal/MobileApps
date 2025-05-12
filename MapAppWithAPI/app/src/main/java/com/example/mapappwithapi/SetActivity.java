package com.example.mapappwithapi;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SetActivity extends FragmentActivity implements OnMapReadyCallback { // Corrected curly brace

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set); // Corrected layout name and semicolon
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map); // Corrected ID and variable name
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Corrected method call and callback syntax
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // Corrected parameter name and curly brace
        mMap = googleMap;

        LatLng fenerbahceStadium = new LatLng(40.987001, 29.034972); // Corrected LatLng constructor syntax
        mMap.addMarker(new MarkerOptions().position(fenerbahceStadium).title("Fenerbahçe Atatürk Stadyumu")); // Corrected Turkish characters
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fenerbahceStadium, 15)); // Corrected zoom syntax and semicolon
    }
}