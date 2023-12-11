package com.example.shareloc.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.shareloc.managers.GeoJsonManager;
import com.example.shareloc.R;
import com.example.shareloc.Class.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class FranceMapActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
    private static final float LOCATION_THRESHOLD_DISTANCE = 500;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private TextView tvPercentageCovered;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupMapFragment();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationCallback();
        tvPercentageCovered = findViewById(R.id.tvPercentageCovered);
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void enableLocationFeatures() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
            }
        }
    }

    @Override
    protected int getLayoutId() {return R.layout.activity_france_map;}

    private String loadJSONFromRawResource(int resourceId) {
        String json;
        try {
            InputStream is = getResources().openRawResource(resourceId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private double calculateExploredPercentage(long numberOfPositionsFound) {
        double totalArea = Math.PI * Math.pow(500, 2) * numberOfPositionsFound;
        double franceArea = 551695000000.0;
        return (totalArea / franceArea) * 100.0;
    }

    @SuppressLint("SetTextI18n")
    private void displayExploredPercentage(double exploredPercentage) {
        tvPercentageCovered.setText("Percentage covered: " + exploredPercentage + "%");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng franceCenter = new LatLng(46.2276, 2.2137);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(franceCenter, 5));

        LatLngBounds franceBounds = new LatLngBounds(
                new LatLng(44.331, -4.141), // Southwest corner of France
                new LatLng(51.089, 7.233)   // Northeast corner of France
        );

        mMap.setLatLngBoundsForCameraTarget(franceBounds);

        mMap.setMinZoomPreference(5.6f);
        mMap.setMaxZoomPreference(15.0f);

        String customMapStyle = loadJSONFromRawResource(R.raw.my_custom_style);

        if (customMapStyle != null) {
            mMap.setMapStyle(new MapStyleOptions(customMapStyle));
        }

        GeoJsonManager geoJsonManager = new GeoJsonManager(this, mMap);
        List<String> country_list = Arrays.asList("luxembourg", "germany", "ireland", "belgium", "united-kingdom", "italy", "spain", "portugal", "switzerland", "netherlands", "austria");
        for (String country : country_list) {
            geoJsonManager.loadGeoJsonLayer(country + ".geojson");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            enableLocationFeatures();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        fetchAndDisplayCurrentUserLocations();
        fetchAndDisplayAllUsersLocations();
        fetchAndCalculateExploredPercentage();
    }

    private void fetchAndCalculateExploredPercentage() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
            userRef.child("positions_found").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long numberOfPositionsFound = dataSnapshot.getChildrenCount();
                    double exploredPercentage = calculateExploredPercentage(numberOfPositionsFound);
                    displayExploredPercentage(exploredPercentage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("FranceMapActivity", "fetchAndCalculateExploredPercentage:onCancelled", databaseError.toException());
                }
            });
        }
    }
    private void fetchAndDisplayCurrentUserLocations() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            userRef.child("positions_found").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot positionSnapshot : dataSnapshot.getChildren()) {
                        UserLocation location = positionSnapshot.getValue(UserLocation.class);
                        if (location != null) {
                            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addCircle(new CircleOptions()
                                    .center(userLatLng)
                                    .radius(500) // Radius in meters
                                    .strokeColor(Color.RED)
                                    .fillColor(Color.argb(70, 255, 0, 0))
                                    .strokeWidth(2f));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FranceMapActivity", "Error fetching current user locations", databaseError.toException());
                }
            });
        }
    }



    private void fetchAndDisplayAllUsersLocations() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot lastUpdatedPosSnapshot = userSnapshot.child("lastUpdatedPosition");
                    if (lastUpdatedPosSnapshot.exists()) {
                        double latitude = lastUpdatedPosSnapshot.child("latitude").getValue(Double.class);
                        double longitude = lastUpdatedPosSnapshot.child("longitude").getValue(Double.class);
                        LatLng userLocation = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(userLocation).title(userSnapshot.child("username").getValue(String.class)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FranceMapActivity", "Error fetching user locations", databaseError.toException());
            }
        });
    }

    private void setupLocationCallback() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateUserLastLocation(location);
                    checkAndSaveNewLocation(location);
                }
            }
        };
        startLocationUpdates(locationRequest);
    }

    private void updateUserLastLocation(Location location) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUser.getUid())
                    .child("lastUpdatedPosition");

            Map<String, Object> locationUpdate = new HashMap<>();
            locationUpdate.put("latitude", location.getLatitude());
            locationUpdate.put("longitude", location.getLongitude());
            userRef.updateChildren(locationUpdate);
        }
    }

    private void checkAndSaveNewLocation(Location newLocation) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference positionsRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUser.getUid())
                    .child("positions_found");

            positionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserLocation savedLocation = snapshot.getValue(UserLocation.class);
                        if (savedLocation != null && savedLocation.isNearby(newLocation, LOCATION_THRESHOLD_DISTANCE)) {
                            return;
                        }
                    }
                    saveNewUserLocation(newLocation, positionsRef);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FranceMapActivity", "Error checking new location", databaseError.toException());
                }
            });
        }
    }

    private void saveNewUserLocation(Location location, DatabaseReference ref) {
        UserLocation newUserLocation = UserLocation.fromLocation(location);
        String key = ref.push().getKey();
        if (key != null) {
            ref.child(key).setValue(newUserLocation);
        }
    }

    private void startLocationUpdates(LocationRequest locationRequest) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
