package com.example.shareloc;

import android.location.Location;

public class UserLocation {
    private double latitude;
    private double longitude;
    public UserLocation() {
    }
    public UserLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
    public static UserLocation fromLocation(Location location) {
        return new UserLocation(location.getLatitude(), location.getLongitude());
    }

    public Location toLocation() {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
}

