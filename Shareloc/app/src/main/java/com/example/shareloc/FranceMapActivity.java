package com.example.shareloc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;


public class FranceMapActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Circle visibilityCircle;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupMapFragment();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationCallback();
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

    private void displayLocationsOnMap(List<Location> locations) {
        for (Location location : locations) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(location.getLatitude(), location.getLongitude()))
                    .radius(500)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(70, 255, 0, 0))
                    .strokeWidth(2f)
                    .zIndex(1.0f);

            mMap.addCircle(circleOptions);
        }
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng franceCenter = new LatLng(46.2276, 2.2137);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(franceCenter, 5));

        LatLngBounds franceBounds = new LatLngBounds(
                new LatLng(42.331, -5.141), // Southwest corner of France
                new LatLng(51.089, 8.233)   // Northeast corner of France
        );

        mMap.setLatLngBoundsForCameraTarget(franceBounds);

        // Set minimum and maximum zoom
        mMap.setMinZoomPreference(5.0f);
        mMap.setMaxZoomPreference(15.0f);

        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.my_custom_style));
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }


        try {
            InputStream inputStream = getAssets().open("challenge/france.geojson");
            GeoJsonLayer franceLayer = new GeoJsonLayer(mMap, new JSONObject(convertStreamToString(inputStream)));
            franceLayer.addLayerToMap();

            for (GeoJsonFeature feature : franceLayer.getFeatures()) {
                GeoJsonPolygonStyle style = new GeoJsonPolygonStyle();
                style.setFillColor(Color.GRAY);
                feature.setPolygonStyle(style);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            enableLocationFeatures();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        visibilityCircle = mMap.addCircle(new CircleOptions()
                .center(franceCenter)
                .radius(1000)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(Color.argb(70, 0, 0, 0)));

        fetchAndDisplayUserLocations();

    }

    private void fetchAndDisplayUserLocations() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
            userRef.child("positions_found").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Location> locations = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserLocation userLocation = snapshot.getValue(UserLocation.class);
                        if (userLocation != null) {
                            locations.add(userLocation.toLocation());
                        }
                    }
                    displayLocationsOnMap(locations);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("FranceMapActivity", "loadPositionsFound:onCancelled", databaseError.toException());
                }
            });
        }
    }

    private void setupLocationCallback() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // Update location every 10 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    checkAndSaveNewLocation(location);
                }
            }
        };
        startLocationUpdates(locationRequest);
    }

    private void checkAndSaveNewLocation(Location newLocation) {
        UserLocation newUserLocation = UserLocation.fromLocation(newLocation); // Convert to UserLocation
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
            userRef.child("positions_found").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isNearbyLocation = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserLocation savedUserLocation = snapshot.getValue(UserLocation.class);
                        if (savedUserLocation != null) {
                            Location savedLocation = savedUserLocation.toLocation(); // Convert to Android Location
                            if (savedLocation.distanceTo(newLocation) < 500) {
                                isNearbyLocation = true;
                                break;
                            }
                        }
                    }
                    if (!isNearbyLocation) {
                        String key = userRef.child("positions_found").push().getKey();
                        if (key != null) {
                            userRef.child("positions_found").child(key).setValue(newUserLocation); // Save UserLocation
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("FranceMapActivity", "checkAndSaveNewLocation:onCancelled", databaseError.toException());
                }
            });
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private void startLocationUpdates(LocationRequest locationRequest) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
