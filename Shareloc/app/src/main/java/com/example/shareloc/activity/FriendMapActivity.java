package com.example.shareloc.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.shareloc.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private String friendUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_map);

        friendUserId = getIntent().getStringExtra("friendId");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("FriendMapActivity", "onMapReady called");
        googleMap = map;

        DatabaseReference friendLocationRef = FirebaseDatabase.getInstance().getReference("userLocations").child(friendUserId);
        friendLocationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("FriendMapActivity", "onDataChange called");

                if (dataSnapshot.exists()) {
                    double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    Log.d("FriendMapActivity", "Latitude: " + latitude + ", Longitude: " + longitude);

                    LatLng friendLocation = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(friendLocation).title("Friend's Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(friendLocation));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                } else {
                    Log.e("FriendMapActivity", "Friend's location data not available");
                    Toast.makeText(FriendMapActivity.this, "Friend's location data not available", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendMapActivity", "onCancelled called: " + databaseError.getMessage());
                Toast.makeText(FriendMapActivity.this, "Error loading friend's location data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}