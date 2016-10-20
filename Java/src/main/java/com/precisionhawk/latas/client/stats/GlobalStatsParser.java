package com.precisionhawk.latas.client.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GlobalStatsParser {

    private final Collection<DroneRawStats> droneRawStatsCollection;

    public GlobalStatsParser(Collection<DroneRawStats> droneRawStatsCollection) {
        this.droneRawStatsCollection = droneRawStatsCollection;
    }

    public GlobalParsedStats parse() {
        GlobalParsedStats globalParsedStats = new GlobalParsedStats();
        double globalAvgMqttLatency = 0;
        double globalAvgStompLatency = 0;
        double globalAvgMissingStomp = 0;
        int mqttCount = 0;
        int noServiceCount = 0;
        List<DroneParsedStats> droneParsedStatsList = new ArrayList<>();
        for (DroneRawStats ds : droneRawStatsCollection) {
            DroneStatsParser droneStatsParser = new DroneStatsParser(ds);
            DroneParsedStats droneParsedStats = droneStatsParser.parse();
            droneParsedStatsList.add(droneParsedStats);
            globalAvgMqttLatency += droneParsedStats.getMqttLatency();
            Double stompLatency = droneParsedStats.getStompLatency();
            if (stompLatency != null) {
                globalAvgStompLatency += stompLatency;
                mqttCount += 1;
            }
            globalAvgMissingStomp += droneParsedStats.getStompLagCount();

            if (droneParsedStats.getStompLagPct() == 100) {
                noServiceCount += 1;
            }
        }
        int size = droneRawStatsCollection.size();
        globalAvgMqttLatency /= size;
        globalAvgStompLatency /= mqttCount;
        globalAvgMissingStomp /= size;
        globalParsedStats.setGlobalAvgMqttLatency(globalAvgMqttLatency);
        globalParsedStats.setGlobalAvgStompLatency(globalAvgStompLatency);
        globalParsedStats.setDroneParsedStatsList(droneParsedStatsList);
        globalParsedStats.setGlobalAvgMissingStomp(globalAvgMissingStomp);
        globalParsedStats.setNoServicePct((int) (100.0 * noServiceCount / size));
        return globalParsedStats;
    }
}
