package com.precisionhawk.latas.client.net;

import com.precisionhawk.latas.client.model.DronePacket;
import com.precisionhawk.latas.client.model.NotificationPacket;

public class StompResponse {

    private DronePacket dronePacket;
    private NotificationPacket notificationPacket;

    public DronePacket getDronePacket() {
        return dronePacket;
    }

    void setDronePacket(DronePacket dronePacket) {
        this.dronePacket = dronePacket;
    }

    public NotificationPacket getNotificationPacket() {
        return notificationPacket;
    }

    void setNotificationPacket(NotificationPacket notificationPacket) {
        this.notificationPacket = notificationPacket;
    }

    @Override
    public String toString() {
        return "StompResponse{" +
            "dronePacket=" + dronePacket +
            ", notificationPacket=" + notificationPacket +
            '}';
    }
}
