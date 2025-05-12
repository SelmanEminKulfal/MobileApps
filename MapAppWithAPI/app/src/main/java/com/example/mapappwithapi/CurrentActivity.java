package com.example.mapappwithapi;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


public class CurrentActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current); // Corrected layout name

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // Corrected context
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map); // Corrected ID and variable name
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Corrected method call
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // Corrected parameter name
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { // Corrected method name and condition
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE); // Corrected array syntax and request code
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() { // Corrected syntax
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude()); // Corrected keyword
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Şu anki konum")); // Corrected Turkish character
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15)); // Corrected zoom syntax
                } else {
                    Toast.makeText(CurrentActivity.this, "Konum bulunamadı", Toast.LENGTH_SHORT).show(); // Corrected Turkish character
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // Corrected syntax
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) { // Using the defined constant
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Corrected permission granted check
                // Permission granted, get the map and location
                onMapReady(mMap);
            } else {
                Toast.makeText(this, "İzin gerekli", Toast.LENGTH_SHORT).show(); // Corrected Turkish character
            }
        }
    }
}