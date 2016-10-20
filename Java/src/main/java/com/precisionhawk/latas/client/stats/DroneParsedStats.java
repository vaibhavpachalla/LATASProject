package com.precisionhawk.latas.client.stats;

import java.util.List;

class DroneParsedStats {

    private Double mqttLatency;
    private Double stompLatency;
    private int numPackets;
    private DroneRawStats droneRawStats;
    private List<DroneTelemetryRoundTrip> roundTrips;
    private int stompLagCount;
    private int stompLagPct;

    void setMqttLatency(Double mqttLatency) {
        this.mqttLatency = mqttLatency;
    }

    Double getMqttLatency() {
        return mqttLatency;
    }

    void setNumPackets(int numPackets) {
        this.numPackets = numPackets;
    }

    public int getNumPackets() {
        return numPackets;
    }

    void setDroneRawStats(DroneRawStats droneRawStats) {
        this.droneRawStats = droneRawStats;
    }

    DroneRawStats getDroneRawStats() {
        return droneRawStats;
    }

    void setRoundTrips(List<DroneTelemetryRoundTrip> roundTrips) {
        this.roundTrips = roundTrips;
    }

    List<DroneTelemetryRoundTrip> getRoundTrips() {
        return roundTrips;
    }

    Double getStompLatency() {
        return stompLatency;
    }

    void setStompLatency(Double stompLatency) {
        this.stompLatency = stompLatency;
    }

    int getStompLagCount() {
        return stompLagCount;
    }

    void setStompLagCount(int stompLagCount) {
        this.stompLagCount = stompLagCount;
    }

    public void setStompLagPct(int stompLagPct) {
        this.stompLagPct = stompLagPct;
    }

    public int getStompLagPct() {
        return stompLagPct;
    }
}
