package com.precisionhawk.latas.client.stats;

import com.precisionhawk.latas.client.model.DronePacket;
import com.precisionhawk.latas.client.net.StompResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

import static java.util.stream.Collectors.averagingDouble;

class DroneStatsParser {

    private final DroneRawStats droneRawStats;

    DroneStatsParser(DroneRawStats droneRawStats) {
        this.droneRawStats = droneRawStats;
    }

    DroneParsedStats parse() {
        List<MessageStats<DronePacket>> dronePackets = droneRawStats.getDronePackets();
        List<MessageStats<StompResponse>> stompResponses = droneRawStats.getStompResponses();
        List<DroneTelemetryRoundTrip> roundTrips = new ArrayList<>();
        for (int i = 0; i < dronePackets.size(); i++) {
            MessageStats<DronePacket> dronePacketStats = dronePackets.get(i);
            MessageStats<StompResponse> stompResponse = null;
            if (i < stompResponses.size()) {
                stompResponse = stompResponses.get(i);
            }
            DroneTelemetryRoundTrip droneTelemetryRoundTrip = new DroneTelemetryRoundTrip(dronePacketStats, stompResponse);
            roundTrips.add(droneTelemetryRoundTrip);
        }

        DroneParsedStats droneParsedStats = new DroneParsedStats();
        int missingStompCount = dronePackets.size() - stompResponses.size();
        droneParsedStats.setStompLagCount(missingStompCount);

        int stompLagPct = (int) (100.0 * missingStompCount / dronePackets.size());
        droneParsedStats.setStompLagPct(stompLagPct);

        ToDoubleFunction<MessageStats<?>> getElapsed = MessageStats::getElapsed;
        Collector<MessageStats<?>, ?, Double> averagingDouble = averagingDouble(getElapsed);
        Double mqttLatency = dronePackets.stream().collect(averagingDouble);
        droneParsedStats.setMqttLatency(mqttLatency);

        Double stompLatency = stompResponses.stream().collect(averagingDouble);
        droneParsedStats.setStompLatency(stompLatency);

        droneParsedStats.setNumPackets(dronePackets.size());
        droneParsedStats.setDroneRawStats(droneRawStats);
        droneParsedStats.setRoundTrips(roundTrips);
        return droneParsedStats;
    }
}
