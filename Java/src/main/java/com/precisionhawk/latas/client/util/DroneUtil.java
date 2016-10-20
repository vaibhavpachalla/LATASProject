package com.precisionhawk.latas.client.util;

import com.precisionhawk.latas.client.model.Drone;
import com.precisionhawk.latas.client.model.DroneProperties;
import com.precisionhawk.latas.client.model.Flock;

import java.util.Properties;

public final class DroneUtil {

    private DroneUtil() {
    }

    public static void populateDroneProperties(DroneProperties droneProperties, Properties properties) {
        droneProperties.setId(getIntProperty(properties, "id"));
        populateDrone(droneProperties, properties);
    }

    private static void populateDrone(Drone drone, Properties properties) {
        drone.setLat(getDoubleProperty(properties, "lat"));
        drone.setLon(getDoubleProperty(properties, "lon"));
        drone.setAlt(getDoubleProperty(properties, "alt"));
        drone.setTrack(getDoubleProperty(properties, "track"));
        drone.setSpeed(getDoubleProperty(properties, "speed"));
        drone.setBattery(getDoubleProperty(properties, "battery"));
    }

    public static void populateFlock(Flock flock, Properties properties) {
        flock.setSeparation(getDoubleProperty(properties, "separation"));
        flock.setSize(getIntProperty(properties, "size"));
        populateDrone(flock, properties);
    }

    private static double getDoubleProperty(Properties properties, String name) {
        return Double.parseDouble(properties.getProperty(name));
    }

    private static int getIntProperty(Properties properties, String name) {
        return Integer.parseInt(properties.getProperty(name));
    }
}
