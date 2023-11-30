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

                Map<String, String> allGeoJsonData = loadAllGeoJsonFromAssets();

                for (String country : allGeoJsonData.keySet()) {
                        String geoJsonData = allGeoJsonData.get(country);
                        if (geoJsonData != null) {
                                addGeoJsonLayerToMap(mMap, geoJsonData);
                        }
                }
        }

        private void addGeoJsonLayerToMap(GoogleMap map, String geoJsonData) {
                try {
                        JSONObject geoJson = new JSONObject(geoJsonData);
                        GeoJsonLayer layer = new GeoJsonLayer(map, geoJson);

                        GeoJsonPolygonStyle style = layer.getDefaultPolygonStyle();
                        style.setFillColor(Color.BLACK);
                        style.setStrokeColor(Color.BLACK);
                        style.setStrokeWidth(2f);

                        layer.addLayerToMap();

                } catch (Exception e) {
                        Log.e("MapActivity", "Problem reading GeoJSON file", e);
                }
        }

        private Map<String, String> loadAllGeoJsonFromAssets() {
                Map<String, String> geoJsonMap = new HashMap<>();

                try {
                        String[] fileList = getAssets().list("");
                        if (fileList != null) {
                                for (String filename : fileList) {
                                        if (filename.endsWith(".geojson")) {
                                                InputStream is = getAssets().open(filename);
                                                int size = is.available();
                                                byte[] buffer = new byte[size];
                                                is.read(buffer);
                                                is.close();
                                                String geoJsonData = new String(buffer, "UTF-8");
                                                geoJsonMap.put(filename.replace(".geojson", ""), geoJsonData);
                                        }
                                }
                        }
                } catch (IOException ex) {
                        ex.printStackTrace();
                }

                return geoJsonMap;
        }
}