package com.precisionhawk.latas.client.model;

import java.util.Arrays;

public class NotificationPacket {

    private int[] droneIds;
    private boolean isLowBattery;
    private boolean isDroneCollision;
    private boolean isGeofenceViolation;

    public int[] getDroneIds() {
        return droneIds;
    }

    public void setDroneIds(int[] droneIds) {
        this.droneIds = droneIds;
    }

    public boolean isLowBattery() {
        return isLowBattery;
    }

    public void setLowBattery(boolean lowBattery) {
        isLowBattery = lowBattery;
    }

    public boolean isDroneCollision() {
        return isDroneCollision;
    }

    public void setDroneCollision(boolean droneCollision) {
        isDroneCollision = droneCollision;
    }

    public boolean isGeofenceViolation() {
        return isGeofenceViolation;
    }

    public void setGeofenceViolation(boolean geofenceViolation) {
        isGeofenceViolation = geofenceViolation;
    }

    @Override
    public String toString() {
        return "NotificationPacket{" +
            "droneIds=" + Arrays.toString(droneIds) +
            ", isLowBattery=" + isLowBattery +
            ", isDroneCollision=" + isDroneCollision +
            ", isGeofenceViolation=" + isGeofenceViolation +
            '}';
    }
}
