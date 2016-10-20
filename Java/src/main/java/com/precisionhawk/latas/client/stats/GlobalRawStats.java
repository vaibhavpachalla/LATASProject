package com.precisionhawk.latas.client.stats;

import com.precisionhawk.latas.client.model.DronePacket;
import com.precisionhawk.latas.client.net.StompResponse;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalRawStats {

    private static GlobalRawStats instance = new GlobalRawStats();

    private Map<Integer, DroneRawStats> map = new ConcurrentHashMap<>();
    private long start;
    private long end;

    private GlobalRawStats() {
    }

    public static GlobalRawStats getInstance() {
        return instance;
    }

    public void markStartTime() {
        start = System.currentTimeMillis();
    }

    public void markEndTime() {
        end = System.currentTimeMillis();
    }

    public long getElapsed() {
        return end - start;
    }

    public DroneRawStats getDroneStats(int droneId) {
        return map.computeIfAbsent(droneId, DroneRawStats::new);
    }

    public Collection<DroneRawStats> getAllDroneStats() {
        return map.values();
    }

    public void stompReceived(StompResponse stompResponse) {
        DronePacket dronePacket = stompResponse.getDronePacket();
        int id = dronePacket.getId();
        DroneRawStats droneRawStats = map.get(id);
        droneRawStats.stompReceived(stompResponse);
    }
}
