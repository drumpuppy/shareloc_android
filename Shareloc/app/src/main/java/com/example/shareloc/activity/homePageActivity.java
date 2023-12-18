package com.example.shareloc.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.shareloc.managers.GeoJsonManager;
import com.example.shareloc.R;
import com.example.shareloc.Class.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class homePageActivity extends BaseActivity implements OnMapReadyCallback {
        private GoogleMap mMap;
        private User currentUser;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        private FusedLocationProviderClient fusedLocationClient;
        private LocationCallback locationCallback;
        private GeoJsonManager geoJsonManager;
        private ExecutorService executorService;


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                setupMapFragment();
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                setupLocationCallback();
                geoJsonManager = new GeoJsonManager(this, mMap);

                ImageView refreshButton = findViewById(R.id.refresh);
                refreshButton.setOnClickListener(view -> refreshMap());
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
        protected int getLayoutId() {
                return R.layout.home_page;
        }
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                geoJsonManager = new GeoJsonManager(this, mMap);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                        enableLocationFeatures();
                } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
                }
                fetchAndDisplayAllUsersLocations();
                loadCurrentUserAndSetupMap();
        }

        private void fetchAndDisplayAllUsersLocations() {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        userRef.child("friendList").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                                                        String friendUserId = friendSnapshot.getValue(String.class);
                                                        if (friendUserId != null) {
                                                                executorService.submit(() -> {
                                                                        fetchFriendLocation(friendUserId);
                                                                });
                                                        } else {
                                                                Log.d("homePageActivity", "Invalid friend ID found: " + friendSnapshot.getKey());
                                                        }
                                                }
                                        } else {
                                                Log.d("homePageActivity", "No friends found for the user.");
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("homePageActivity", "Error fetching friends list", databaseError.toException());
                                }
                        });
                } else {
                        Log.w("homePageActivity", "FirebaseUser is null");
                }
        }



        private void fetchFriendLocation(String friendUserId) {
                DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("users").child(friendUserId);

                friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                        // Fetch username
                                        String username = dataSnapshot.child("username").getValue(String.class);

                                        // Fetch location
                                        DataSnapshot locationSnapshot = dataSnapshot.child("lastUpdatedPosition");
                                        if (locationSnapshot.exists()) {
                                                double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                                                double longitude = locationSnapshot.child("longitude").getValue(Double.class);
                                                LatLng friendLocation = new LatLng(latitude, longitude);

                                                // Add marker with username as title
                                                mMap.addMarker(new MarkerOptions().position(friendLocation).title(username));
                                                Log.d("homePageActivity", "Added marker for " + username + "'s location: " + friendLocation);
                                        } else {
                                                Log.d("homePageActivity", "Location data not found for friend with ID: " + friendUserId);
                                        }
                                } else {
                                        Log.d("homePageActivity", "Friend data not found for ID: " + friendUserId);
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("homePageActivity", "Error fetching friend's data", databaseError.toException());
                        }
                });
        }


        private void loadCurrentUserAndSetupMap() {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        currentUser = dataSnapshot.getValue(User.class);
                                        if (currentUser != null) {
                                                setupCountryOverlays();
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.w("homePageActivity", "loadCurrentUser:onCancelled", databaseError.toException());
                                }
                        });
                } else {
                        Log.w("homePageActivity", "FirebaseUser is null");
                }
        }
        private void setupCountryOverlays() {
                if (currentUser != null && currentUser.getCountriesVisited() != null) {
                        Map<String, Boolean> visitedCountries = currentUser.getCountriesVisited();
                        for (String country : visitedCountries.keySet()) {
                                if (Boolean.FALSE.equals(visitedCountries.get(country))) {
                                        executorService.submit(() -> {
                                                geoJsonManager.loadGeoJsonLayer(country + ".geojson");
                                        });
                                }
                        }
                }
        }


        @Override
        protected void onDestroy() {
                super.onDestroy();
                if (executorService != null && !executorService.isShutdown()) {
                        executorService.shutdown();
                }
        }

        private void setupLocationCallback() {
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(10000);

                locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                                for (Location location : locationResult.getLocations()) {
                                        String countryName = getCountryName(location.getLatitude(), location.getLongitude());
                                        updateCountryVisited(countryName);
                                }
                        }
                };
                startLocationUpdates(locationRequest);
        }
        private void startLocationUpdates(LocationRequest locationRequest) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                        return;
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }

        private String getCountryName(double latitude, double longitude) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                GeoJsonManager geoJsonManager = new GeoJsonManager(this, mMap);
                try {
                        String countryName = Objects.requireNonNull(geocoder.getFromLocation(latitude, longitude, 1)).get(0).getCountryName();
                        return geoJsonManager.mapCountryNameToFileName(countryName);
                } catch (Exception e) {
                        Log.e("homePageActivity", "Geocoder failed", e);
                        return null;
                }
        }

        private void updateCountryVisited(String countryFileName) {
                if (countryFileName == null) return;

                String firebaseKey = countryFileName.replace(".geojson", "").replaceAll("[.#$\\[\\]]", "");
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseUser != null) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        userRef.child("countriesVisited").child(firebaseKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists() || dataSnapshot.getValue(Boolean.class) == Boolean.FALSE) {
                                                userRef.child("countriesVisited").child(firebaseKey).setValue(true);
                                                refreshMap();
                                                Toast.makeText(homePageActivity.this, "Vous venez de découvrir : " + firebaseKey + "!", Toast.LENGTH_LONG).show();
                                        }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.w("homePageActivity", "Database error: " + databaseError.toException());
                                }
                        });
                }
        }


        private void refreshMap() {
                if (mMap == null) {
                        Log.e("homePageActivity", "Map is not ready to be refreshed");
                        return;
                }
                mMap.clear();
                fetchAndDisplayAllUsersLocations();
                loadCurrentUserAndSetupMap();
        }
}