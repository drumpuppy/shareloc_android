package com.example.shareloc;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import android.Manifest;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class homePageActivity extends BaseActivity implements OnMapReadyCallback {
        private GoogleMap mMap;
        private User currentUser;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setupMapFragment();
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
                        is.read(buffer);
                        is.close();
                        return new String(buffer, "UTF-8");
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
}