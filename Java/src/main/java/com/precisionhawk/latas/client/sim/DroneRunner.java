package com.precisionhawk.latas.client.sim;

import com.precisionhawk.latas.client.model.DroneProperties;
import com.precisionhawk.latas.client.net.DroneMqttPublisher;

class DroneRunner implements Runnable {

    private final DroneSim droneSim;
    private final DroneMqttPublisher droneMqttPublisher;

    DroneRunner(DroneProperties droneProperties, DroneMqttPublisher droneMqttPublisher) {
        this.droneSim = new DroneSim(droneProperties);
        this.droneMqttPublisher = droneMqttPublisher;
    }

    int getDroneId() {
        return droneMqttPublisher.getDroneId();
    }

    public void run() {
        droneSim.tick();
        droneMqttPublisher.publishMqtt(droneSim.getDrone());
    }

    void shutdown() {
        droneMqttPublisher.publishLastMqtt(droneSim.getDrone());
    }
}
