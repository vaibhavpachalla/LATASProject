package com.precisionhawk.latas.client.stats;

import java.util.List;

public class GlobalParsedStats {

    private List<DroneParsedStats> droneParsedStatsList;
    private double globalAvgMqttLatency; // time to send MQTT message
    private double globalAvgStompLatency; // time between MQTT sent and STOMP received
    private double globalAvgMissingStomp;
    private int noServicePct;

    void setGlobalAvgMqttLatency(double globalAvgMqttLatency) {
        this.globalAvgMqttLatency = globalAvgMqttLatency;
    }

    double getGlobalAvgMqttLatency() {
        return globalAvgMqttLatency;
    }

    void setDroneParsedStatsList(List<DroneParsedStats> droneParsedStatsList) {
        this.droneParsedStatsList = droneParsedStatsList;
    }

    List<DroneParsedStats> getDroneParsedStatsList() {
        return droneParsedStatsList;
    }

    double getGlobalAvgStompLatency() {
        return globalAvgStompLatency;
    }

    void setGlobalAvgStompLatency(double globalAvgStompLatency) {
        this.globalAvgStompLatency = globalAvgStompLatency;
    }

    void setGlobalAvgMissingStomp(double globalAvgMissingStomp) {
        this.globalAvgMissingStomp = globalAvgMissingStomp;
    }

    double getGlobalAvgMissingStomp() {
        return globalAvgMissingStomp;
    }

    public void setNoServicePct(int noServicePct) {
        this.noServicePct = noServicePct;
    }

    public int getNoServicePct() {
        return noServicePct;
    }
}
