package com.example.shareloc.Class;

import android.location.Location;

public class UserLocation {
    private double latitude;
    private double longitude;

    public UserLocation() {
        this.latitude = 0.0;
        this.longitude = 0.0;
    }
    public UserLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static UserLocation fromLocation(Location location) {
        return new UserLocation(location.getLatitude(), location.getLongitude());
    }

    public boolean isNearby(Location location, float thresholdDistance) {
        float[] results = new float[1];
        Location.distanceBetween(this.latitude, this.longitude, location.getLatitude(), location.getLongitude(), results);
        return results[0] < thresholdDistance;
    }
    public Location toLocation() {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

