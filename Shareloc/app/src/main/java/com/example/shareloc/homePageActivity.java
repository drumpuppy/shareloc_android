package com.example.shareloc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;


import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class homePageActivity extends BaseActivity implements OnMapReadyCallback {
        private GoogleMap mMap;
        private User currentUser;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        private FusedLocationProviderClient fusedLocationClient;
        private LocationCallback locationCallback;

        //génère la map google api
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
        protected int getLayoutId() {
                return R.layout.home_page;
        }
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                        enableLocationFeatures();
                } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
                }

                loadCurrentUserAndSetupMap();
        }


        // load countries layout
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
                        Log.w("---------------> visited countries :\n",visitedCountries.toString());
                        for (String country : visitedCountries.keySet()) {
                                if (Boolean.FALSE.equals(visitedCountries.get(country))) {
                                        String geoJsonData = loadGeoJsonFromAsset(country + ".geojson");
                                        if (geoJsonData != null) {
                                                addGeoJsonLayerToMap(mMap, geoJsonData, country);
                                        }
                                }
                        }
                }
        }
        private String loadGeoJsonFromAsset(String filename) {
                try {
                        InputStream is = getAssets().open(filename);
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        int bytesRead = is.read(buffer);
                        if (bytesRead != size) {
                                Log.w("homePageActivity", "load Geo buffer size !=");
                        }
                        is.close();
                        return new String(buffer, StandardCharsets.UTF_8);
                } catch (IOException ex) {
                        Log.e("homePageActivity", "Error reading GeoJSON file: " + filename, ex);
                        return null;
                }
        }
        private void addGeoJsonLayerToMap(GoogleMap map, String geoJsonData, String countryName) {
                try {
                        JSONObject geoJson = new JSONObject(geoJsonData);
                        GeoJsonLayer layer = new GeoJsonLayer(map, geoJson);
                        GeoJsonPolygonStyle style = layer.getDefaultPolygonStyle();
                        style.setFillColor(Color.BLACK);
                        style.setStrokeColor(Color.BLACK);
                        style.setStrokeWidth(2f);
                        layer.addLayerToMap();
                } catch (Exception e) {
                        Log.e("homePageActivity", "Problem reading GeoJSON file for country: " + countryName, e);
                }
        }



        // find where you are
        private void setupLocationCallback() {
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(10000); // Update location every 10 seconds

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
        private String mapCountryNameToFileName(String countryName) {
                Map<String, String> countryNameToFileMap = new HashMap<>();
                countryNameToFileMap.put("Albania", "albania");
                countryNameToFileMap.put("Austria", "austria");
                countryNameToFileMap.put("Belarus", "belarus");
                countryNameToFileMap.put("Belgium", "belgium");
                countryNameToFileMap.put("Bosnia and Herzegovina", "bosnia-and-herzegovina");
                countryNameToFileMap.put("Bulgaria", "bulgaria");
                countryNameToFileMap.put("Croatia", "croatia");
                countryNameToFileMap.put("Cyprus", "cyprus");
                countryNameToFileMap.put("Czech Republic", "czech-republic");
                countryNameToFileMap.put("Denmark", "denmark");
                countryNameToFileMap.put("Estonia", "estonia");
                countryNameToFileMap.put("Finland", "finland");
                countryNameToFileMap.put("France", "france");
                countryNameToFileMap.put("Germany", "germany");
                countryNameToFileMap.put("Greece", "greece");
                countryNameToFileMap.put("Hungary", "hungary");
                countryNameToFileMap.put("Ireland", "ireland");
                countryNameToFileMap.put("Italy", "italy");
                countryNameToFileMap.put("Latvia", "latvia");
                countryNameToFileMap.put("Lithuania", "lithuania");
                countryNameToFileMap.put("Luxembourg", "luxembourg");
                countryNameToFileMap.put("Malta", "malta");
                countryNameToFileMap.put("Moldova", "moldova");
                countryNameToFileMap.put("Montenegro", "montenegro");
                countryNameToFileMap.put("Netherlands", "netherlands");
                countryNameToFileMap.put("Norway", "norway");
                countryNameToFileMap.put("Poland", "poland");
                countryNameToFileMap.put("Portugal", "portugal");
                countryNameToFileMap.put("North Macedonia", "republic-of-north-macedonia");
                countryNameToFileMap.put("Romania", "romania");
                countryNameToFileMap.put("Serbia", "serbia");
                countryNameToFileMap.put("Slovakia", "slovakia");
                countryNameToFileMap.put("Slovenia", "slovenia");
                countryNameToFileMap.put("Spain", "spain");
                countryNameToFileMap.put("Sweden", "sweden");
                countryNameToFileMap.put("Switzerland", "switzerland");
                countryNameToFileMap.put("Ukraine", "ukraine");
                countryNameToFileMap.put("United Kingdom", "united-kingdom");

                String fileName = countryNameToFileMap.get(countryName);
                if (fileName != null) {
                        return fileName + ".geojson";
                } else {
                        Log.w("homePageActivity", "Country name mapping not found for: " + countryName);
                        return null;
                }
        }
        private String getCountryName(double latitude, double longitude) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                        String countryName = Objects.requireNonNull(geocoder.getFromLocation(latitude, longitude, 1)).get(0).getCountryName();
                        return mapCountryNameToFileName(countryName);
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
                loadCurrentUserAndSetupMap();
        }


}