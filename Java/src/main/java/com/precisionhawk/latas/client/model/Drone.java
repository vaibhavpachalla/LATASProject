package com.precisionhawk.latas.client.model;

public class Drone {

    private double lat;
    private double lon;
    private double alt;
    private double track;
    private double speed;
    private double battery;

    public Drone() {}

    public Drone(Drone drone) {
        lat = drone.lat;
        lon = drone.lon;
        alt = drone.alt;
        track = drone.track;
        speed = drone.speed;
        battery = drone.battery;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double getTrack() {
        return track;
    }

    public void setTrack(double track) {
        this.track = track;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }

    @Override
    public String toString() {
        return "Drone{" +
            "lat=" + lat +
            ", lon=" + lon +
            ", alt=" + alt +
            ", track=" + track +
            ", speed=" + speed +
            ", battery=" + battery +
            '}';
    }
}
