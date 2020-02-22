package com.example.signupapp.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.signupapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

    LocationRequest locationRequest;
    Marker currLocationMarker;

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

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latilngi = new LatLng(address.getLatitude(), address.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(latilngi).title(location);
                    mapCurr.addMarker(markerOptions);
                    mapCurr.animateCamera(CameraUpdateFactory.newLatLngZoom(latilngi, 8));
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


    @SuppressLint("NewApi")
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
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
            }


        }
    }

    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapCurr = googleMap;

        mapCurr.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mapCurr.clear(); //clear old markers

        /*locationRequest = new LocationRequest();
        locationRequest.setInterval(120000); // two minute interval
        locationRequest.setFastestInterval(120000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                mapCurr.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        new AlertDialog.Builder(this)
                                .setTitle("Location Permission Needed")
                                .setMessage("This app needs the Location permission, please accept to use location functionality")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Prompt the user once explanation has been shown
                                        ActivityCompat.requestPermissions(MapsActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_CODE );
                                    }
                                })
                                .create()
                                .show();


                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE );
                    }
                }
            }
        }
        else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            mapCurr.setMyLocationEnabled(true);
        }*/

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

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                currentLocation = location;
                if (currLocationMarker != null) {
                    currLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                currLocationMarker = mapCurr.addMarker(markerOptions);

                //move map camera
                mapCurr.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            }
        }
    };
/*
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }*/

}