package com.precisionhawk.latas.client.stats;

import com.precisionhawk.latas.client.model.DronePacket;
import com.precisionhawk.latas.client.net.StompResponse;

import java.util.OptionalLong;

class DroneTelemetryRoundTrip {

    private final MessageStats<DronePacket> dronePacketStats;
    private final MessageStats<StompResponse> stompStats;

    DroneTelemetryRoundTrip(MessageStats<DronePacket> dronePacketStats, MessageStats<StompResponse> stompStats) {
        this.dronePacketStats = dronePacketStats;
        this.stompStats = stompStats; // may be null
        process();
    }

    private void process() {
        if (stompStats != null) {
            long sendCompleted = dronePacketStats.getTimestamp() + dronePacketStats.getElapsed();
            long stompReceived = stompStats.getTimestamp();
            long roundTripTime = stompReceived - sendCompleted;
            stompStats.setElapsed(roundTripTime);
        }
    }

    MessageStats<DronePacket> getDronePacketStats() {
        return dronePacketStats;
    }

    MessageStats<StompResponse> getStompStats() {
        return stompStats;
    }

    public OptionalLong getRoundTripTime() {
        if (stompStats != null) {
            return OptionalLong.of(stompStats.getElapsed());
        } else {
            return OptionalLong.empty();
        }
    }
}
