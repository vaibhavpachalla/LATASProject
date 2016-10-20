package com.precisionhawk.latas.client.net;

import com.precisionhawk.latas.client.model.Drone;
import com.precisionhawk.latas.client.model.DronePacket;
import com.precisionhawk.latas.client.model.PacketStatus;
import com.precisionhawk.latas.client.stats.DroneRawStats;
import com.precisionhawk.latas.client.stats.GlobalRawStats;
import com.precisionhawk.latas.client.util.ExceptionUtil;
import com.precisionhawk.latas.client.util.JsonUtil;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import javax.net.ssl.SSLSocketFactory;

/**
 * Publishes MQTT messages via the connection and topic contained in LatasSession.
 */
public class DroneMqttPublisher {

    private final int droneId;
    private final LatasSession deviceSession;
    private final DroneRawStats stats;
    private String topic;
    private MqttClient client;
    private int sequence;

    DroneMqttPublisher(int droneId, LatasSession deviceSession) {
        this.droneId = droneId;
        this.deviceSession = deviceSession;
        stats = GlobalRawStats.getInstance().getDroneStats(droneId);
    }

    public int getDroneId() {
        return droneId;
    }

    public void loadTopic() {
        String path = "/api/drones/" + droneId + "/mqtt-topic";
        JSONObject jsonObject = deviceSession.httpGet(path);
        topic = jsonObject.getString("telemetryPublishingTopic");
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * @param ssl
     * @param host e.g. "foo.com"
     */
    public void connectMqtt(boolean ssl, String host) {
        String droneIdStr = String.valueOf(droneId);

        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName("oauth@" + droneIdStr);
        opts.setPassword(deviceSession.getToken().toCharArray());

        String serverUri;
        if (ssl) {
            serverUri = "ssl://" + host + ":8883";
            opts.setSocketFactory(SSLSocketFactory.getDefault());
        } else {
            serverUri = "tcp://" + host;
        }

        ExceptionUtil.duckRunnable(() -> {
            client = new MqttClient(serverUri, droneIdStr);
            client.connect(opts);
        });
    }

    /**
     * Publishes an MQTT packet based on the state of the passed-in Drone.
     */
    public void publishMqtt(Drone drone) {
        publishMqtt(drone, sequence == 0 ? PacketStatus.FIRST : PacketStatus.OK);
    }

    public void publishLastMqtt(Drone drone) {
        publishMqtt(drone, PacketStatus.LAST);
    }

    private void publishMqtt(Drone drone, PacketStatus status) {
        DronePacket dronePacket = new DronePacket(drone);
        dronePacket.setSequence(sequence);
        dronePacket.setId(droneId);
        dronePacket.setStatus(status);
        stats.sending(dronePacket);
        String json = JsonUtil.toJson(dronePacket);
        System.out.print('.');
        ExceptionUtil.duckRunnable(() ->
            client.publish(topic, new MqttMessage(json.getBytes()))
        );
        stats.sent(dronePacket);
        sequence += 1;
    }

    public void close() {
        ExceptionUtil.duckRunnable(() -> {
            client.disconnect();
            client.close();
        });
    }
}
