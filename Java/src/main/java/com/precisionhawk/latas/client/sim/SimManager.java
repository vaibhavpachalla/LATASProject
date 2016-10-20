package com.precisionhawk.latas.client.sim;

import com.precisionhawk.latas.client.model.DroneProperties;
import com.precisionhawk.latas.client.stats.GlobalRawStats;
import com.precisionhawk.latas.client.util.ViewPortExtent;
import com.precisionhawk.latas.client.net.DroneMqttPublisher;
import com.precisionhawk.latas.client.net.LatasSession;
import com.precisionhawk.latas.client.net.StompSubscriber;
import com.precisionhawk.latas.client.util.ConnectionProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SimManager {

    private static final int DELAY = 500;
    private static final int POOL_SIZE = 100;
    private static final double VIEWPORT_PADDING = 0.5;

    private final Map<Integer, DroneRunner> map = new HashMap<>();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(POOL_SIZE);
    private final ViewPortExtent viewPortExtent = new ViewPortExtent();
    private GlobalRawStats globalRawStats = GlobalRawStats.getInstance();

    private final LatasSession deviceSession;
    private final ConnectionProperties connectionProperties;
    private int droneCounter;

    public SimManager(ConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
        deviceSession = new LatasSession(connectionProperties, LatasSession.Scope.DEVICE);
    }

    public void loadOauthToken() {
        deviceSession.loadOauthToken();
    }

    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });
    }

    public void add(DroneProperties droneProperties) {
        viewPortExtent.add(droneProperties);

        DroneMqttPublisher droneMqttPublisher = deviceSession.getDronePublisher(droneProperties.getId());

        System.out.println("connectMqtt[" + droneCounter++ + "]...");
        droneMqttPublisher.connectMqtt(connectionProperties.isSsl(), connectionProperties.getMqttHost());

        System.out.println("loadTopic...");
        droneMqttPublisher.loadTopic();

        DroneRunner droneRunner = new DroneRunner(droneProperties, droneMqttPublisher);
        add(droneRunner);
    }

    private void add(DroneRunner droneRunner) {
        map.put(droneRunner.getDroneId(), droneRunner);
    }

    private void forEachDroneRunner(Consumer<DroneRunner> consumer) {
        map.values().forEach(consumer);
    }

    public void run() {
        GlobalRawStats.getInstance().markStartTime();
        forEachDroneRunner(droneRunner ->
            executor.scheduleWithFixedDelay(droneRunner, 0, DELAY, TimeUnit.MILLISECONDS)
        );
    }

    public void shutdown() {
        executor.shutdownNow();
        forEachDroneRunner(DroneRunner::shutdown);
        globalRawStats.markEndTime();
    }

    public void stompSubscribe() {
        StompSubscriber stompSubscriber = new StompSubscriber(
            deviceSession,
            connectionProperties.getServiceHost()
        );
        stompSubscriber.subscribe(
            viewPortExtent.getViewPortWithPadding(VIEWPORT_PADDING),
            stompResponse -> globalRawStats.stompReceived(stompResponse)
        );
    }

    public void addAll(List<DroneProperties> drones) {
        drones.forEach(this::add);
    }
}
