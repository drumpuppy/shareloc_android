package com.example.shareloc.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.shareloc.R;
import com.example.shareloc.managers.GeoJsonManager;
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

import java.util.Map;

public class FriendMapActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap nMap;
    private String friendUserId;
    private GeoJsonManager geoJsonManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupMapFragment();
        geoJsonManager = new GeoJsonManager(this, nMap);

        friendUserId = getIntent().getStringExtra("friendUserId");
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (nMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    nMap.setMyLocationEnabled(true);
                    nMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
            }
        }
    }

    @Override
    protected int getLayoutId() { return R.layout.activity_friend_map; }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        geoJsonManager = new GeoJsonManager(this, nMap);
        loadFriendCountriesVisited();
    }

    private void loadFriendCountriesVisited() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(friendUserId);
        userRef.child("countriesVisited").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot countrySnapshot : dataSnapshot.getChildren()) {
                        String countryName = countrySnapshot.getKey();
                        Boolean visited = countrySnapshot.getValue(Boolean.class);
                        if (visited != null && !visited) {
                            geoJsonManager.loadGeoJsonLayer(countryName + ".geojson");
                        }
                    }
                } else {
                    Log.d("FriendMapActivity", "No countries visited data found for user ID: " + friendUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendMapActivity", "Error fetching visited countries: " + databaseError.toException());
            }
        });
    }
}
