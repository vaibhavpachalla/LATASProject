package com.precisionhawk.latas.client.model;

public class Location {

    private final double lat;
    private final double lon;

    public Location(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return String.valueOf(lat) + ',' + String.valueOf(lon);
    }
}
