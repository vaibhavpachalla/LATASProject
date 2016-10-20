package com.precisionhawk.latas.client.stats;

import com.precisionhawk.latas.client.model.DronePacket;
import com.precisionhawk.latas.client.net.StompResponse;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DroneRawStats {

    // CopyOnWriteArrayList is slow, but Collections.synchronizedList requires synchronization on iteration.
    private final List<MessageStats<DronePacket>> dronePackets = new CopyOnWriteArrayList<>();
    private final List<MessageStats<StompResponse>> stompResponses = new CopyOnWriteArrayList<>();
    private final int droneId;

    DroneRawStats(int id) {
        droneId = id;
    }

    int getDroneId() {
        return droneId;
    }

    public void sending(DronePacket dronePacket) {
        dronePackets.add(new MessageStats<>(dronePacket));
    }

    public void sent(DronePacket dronePacket) {
        MessageStats latestDronePacketStats = dronePackets.get(dronePackets.size() - 1);
        if (!latestDronePacketStats.getMessage().equals(dronePacket)) {
            throw new RuntimeException("!latestDronePacketStats.getMessage().equals(dronePacket)");
        }
        latestDronePacketStats.setElapsed(System.currentTimeMillis() - latestDronePacketStats.getTimestamp());
    }

    void stompReceived(StompResponse stompResponse) {
        stompResponses.add(new MessageStats<>(stompResponse));
    }

    List<MessageStats<DronePacket>> getDronePackets() {
        return dronePackets;
    }

    List<MessageStats<StompResponse>> getStompResponses() {
        return stompResponses;
    }
}
