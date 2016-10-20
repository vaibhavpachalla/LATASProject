package com.precisionhawk.latas.client.model;

public class DroneProperties extends Drone implements HasLocation {

    private int id;

    public DroneProperties() {
    }

    public DroneProperties(Drone drone) {
        super(drone);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Location getLocation() {
        return new Location(getLat(), getLon());
    }
}
