package com.example.mapsproject;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private double longitude = 151;
    private double latitude = -34;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, Criteria.ACCURACY_COARSE, this);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    // Called when your GPS location changes
    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
        alertDialog.setTitle("Location Detected");
        alertDialog.setMessage(String.valueOf(latitude) + "," + String.valueOf(longitude));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        LatLng loc = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(loc).title("Marker in at your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 12.0f));
    }

    // Called when a provider gets turned off by the user in the settings
    @Override
    public void onProviderDisabled(String provider) {

    }

    // Called when a provider is turned on by the user in the settings
    @Override
    public void onProviderEnabled(String provider) {

    }

    // Signals a state change in the GPS (e.g. you head through a tunnel and it loses its fix on your position
//    @Override
//    public void onStatusChanged(String provider, Int status, Bundle extras) {
//
//    }

    protected void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            Log.e("Err", "No Location update permission remover");
        }
    }

    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 10, Criteria.ACCURACY_COARSE, this);
    }
}