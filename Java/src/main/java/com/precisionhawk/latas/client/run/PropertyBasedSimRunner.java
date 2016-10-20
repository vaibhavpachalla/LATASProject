package com.precisionhawk.latas.client.run;

import com.beust.jcommander.Parameter;
import com.precisionhawk.latas.client.sim.SimManager;
import com.precisionhawk.latas.client.util.ConnectionProperties;
import com.precisionhawk.latas.client.util.PropertiesLoader;

import java.util.ArrayList;
import java.util.List;

public class PropertyBasedSimRunner implements Runnable {

    @Parameter
    private List<String> args = new ArrayList<>();

    public void run() {
        String env = args.get(1);
        PropertiesLoader propertiesLoader = new PropertiesLoader(env);
        ConnectionProperties connectionProperties = propertiesLoader.getConnectionProperties();
        SimManager simManager = new SimManager(connectionProperties);
        simManager.loadOauthToken();
        for (int i = 2; i < args.size(); i++) {
            simManager.add(propertiesLoader.getDroneProperties(args.get(i)));
        }
        simManager.stompSubscribe();
        simManager.run();
    }
}
