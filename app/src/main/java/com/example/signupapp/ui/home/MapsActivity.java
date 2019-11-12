package com.example.signupapp.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.signupapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //List<Address> addressList1 = null;
    GoogleMap mapCurr;
    SupportMapFragment mapFragment;
    SearchView searchView;

    FloatingActionButton btnCurrLocation;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        btnCurrLocation = findViewById(R.id.btnCurrLocation);
        searchView = findViewById(R.id.sv_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frg);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latilngi = new LatLng(address.getLatitude(),address.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(latilngi).title(location);
                    mapCurr.addMarker(markerOptions);
                    mapCurr.animateCamera(CameraUpdateFactory.newLatLngZoom(latilngi, 21));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        btnCurrLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
                fetchLastLocation();
            }

        });
        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener((new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()
                            + ", " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                    LatLng latlng  = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                    CameraPosition googlePlex = CameraPosition.builder()
                            .target(latlng)
                            .zoom(20)
                            .bearing(0)
                            .tilt(45)
                            .build();


                    mapCurr.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 3000, null);
                    MarkerOptions markerOptions = new MarkerOptions().position(latlng).title("You are here!!");
                    mapCurr.addMarker(markerOptions);

                    //Address address1 = addressList1.get(0);
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frg);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        }));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapCurr = googleMap;

        mapCurr.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mapCurr.clear(); //clear old markers

        /*LatLng latlng  = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        CameraPosition googlePlex = CameraPosition.builder()
                .target(latlng)
                .zoom(20)
                .bearing(0)
                .tilt(45)
                .build();

        MarkerOptions markerOptions = new MarkerOptions().position(latlng).title("You are here!!");
        mapCurr.addMarker(markerOptions);
        mapCurr.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);*/

        mapCurr.addMarker(new MarkerOptions()
                .position(new LatLng(19.081300, 72.888600))
                .title("Don Bosco Institute Of Technology")
                .snippet("Mumbai University College")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.dbit_logo)));

        mapCurr.addMarker(new MarkerOptions()
                .position(new LatLng(19.231816, 73.007550))
                .title("Dariya Kinara Restaurant")
                .snippet("Hotel in Bhiwandi")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.bhiwandi)));

        mapCurr.addMarker(new MarkerOptions()
                .position(new LatLng(19.214840, 72.958028))
                .title("Wok & Grill")
                .snippet("Chinese Restaurant")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.decors)));

        mapCurr.addMarker(new MarkerOptions()
                .position(new LatLng(19.224951, 72.958743))
                .title("Diwali Celebration")
                .snippet("Upvan Lake")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.diwali)));

        mapCurr.addMarker(new MarkerOptions()
                .position(new LatLng(19.081199, 72.888533))
                .title("Friends")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.friends)));

        mapCurr.addMarker(new MarkerOptions()
                .position(new LatLng(19.286980, 72.938247))
                .title("Scenery")
                .snippet("Gaimukh, Godhbunder Road")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.gaimukh)));

        mapCurr.addMarker(new MarkerOptions()
                .position(new LatLng(19.219335, 72.953089))
                .title("Mother Mary Statue")
                .snippet("Rosary Session")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.mother_mary)));

        mapCurr.addMarker(new MarkerOptions()
                .position(new LatLng(19.221841, 72.954906))
                .title("Upvan Lake")
                .snippet("Tourist spot")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.upvan)));

        mapCurr.addMarker(new MarkerOptions()
                .position(new LatLng(19.079424,72.897034))
                .title("Vidyavihar Station Road")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.vidyavihar)));
    }
/*
    public void btnCurrLocationFloat(View view) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        fetchLastLocation();


    }*/
}