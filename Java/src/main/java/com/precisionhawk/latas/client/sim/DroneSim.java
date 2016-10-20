package com.precisionhawk.latas.client.sim;

import com.precisionhawk.latas.client.model.Drone;
import com.precisionhawk.latas.client.model.Location;

/**
 * Right now, does almost nothing; just flies the drone in a straight line based on its speed and track.
 */
public class DroneSim {

    private final Drone drone;
    private long lastTickTime;

    public DroneSim(Drone drone) {
        this.drone = new Drone(drone);
    }

    public Drone getDrone() {
        return drone;
    }

    private double getDegreesPerSecond() {
        return drone.getSpeed()/(60.0 * 3600);
    }

    Location getLocation() {
        return new Location(drone.getLat(), drone.getLon());
    }

    /**
     * Moves the member drone depending on its speed (knots) and track.
     */
    public void tick() {
        if (lastTickTime == 0) {
            lastTickTime = System.currentTimeMillis();
            return;
        }

        double radians = Math.toRadians(drone.getTrack());
        double dy = Math.cos(radians);
        double dx = Math.sin(radians);
        // for simplicity, say 1 knot is 1/60 of a degree for x and y, and assume speed is horizontal
        double degreesPerSecond = getDegreesPerSecond();

        long now = System.currentTimeMillis();
        long millisElapsed = now - lastTickTime;
        lastTickTime = now;

        double magnitude = (degreesPerSecond * millisElapsed) / 1000;
        drone.setLat(drone.getLat() + (dy * magnitude));
        drone.setLon(drone.getLon() + (dx * magnitude));
    }
}
