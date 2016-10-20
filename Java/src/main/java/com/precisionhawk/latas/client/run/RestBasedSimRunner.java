package com.precisionhawk.latas.client.run;

import com.beust.jcommander.Parameter;
import com.precisionhawk.latas.client.model.DroneProperties;
import com.precisionhawk.latas.client.model.Flock;
import com.precisionhawk.latas.client.net.LatasSession;
import com.precisionhawk.latas.client.net.LatasSession.Scope;
import com.precisionhawk.latas.client.sim.DroneLoader;
import com.precisionhawk.latas.client.sim.FlockBuilder;
import com.precisionhawk.latas.client.sim.SimManager;
import com.precisionhawk.latas.client.stats.GlobalParsedStats;
import com.precisionhawk.latas.client.stats.GlobalRawStats;
import com.precisionhawk.latas.client.stats.GlobalStatsParser;
import com.precisionhawk.latas.client.stats.StatsServer;
import com.precisionhawk.latas.client.util.ConnectionProperties;
import com.precisionhawk.latas.client.util.DroneUtil;
import com.precisionhawk.latas.client.util.ExceptionUtil;
import com.precisionhawk.latas.client.util.PropertiesLoader;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.lang.System.exit;

public class RestBasedSimRunner implements Runnable {

    @Parameter
    private List<String> args = new ArrayList<>();

    @Parameter(names = "--monitoring")
    private boolean monitoring;

    public void run() {
        String env = args.get(1);
        PropertiesLoader propertiesLoader = new PropertiesLoader(env);
        ConnectionProperties connectionProperties = propertiesLoader.getConnectionProperties();
        LatasSession apiSession = new LatasSession(connectionProperties, Scope.ORGANIZATION_ADMIN);
        apiSession.loadOauthToken();

        Properties flockProperties = propertiesLoader.getProperties("flock");
        Flock flock = new Flock();
        DroneUtil.populateFlock(flock, flockProperties);

        SimManager simManager = new SimManager(connectionProperties);
        simManager.loadOauthToken();

        DroneLoader droneLoader = new DroneLoader(apiSession);
        List<JSONObject> jsonDrones = droneLoader.load(flock.getSize());

        FlockBuilder fb = new FlockBuilder(flock, jsonDrones);
        List<DroneProperties> drones = fb.getDrones();

        simManager.addAll(drones);

        simManager.stompSubscribe();
        simManager.run();

        if (monitoring) {
            ExceptionUtil.duckRunnable(() ->
                Thread.sleep(10_000)
            );
            simManager.shutdown();
            returnStatus();
        } else {
            simManager.addShutdownHook();
            StatsServer.start();
        }
    }

    private void returnStatus() {
        GlobalStatsParser globalStatsParser = new GlobalStatsParser(GlobalRawStats.getInstance().getAllDroneStats());
        GlobalParsedStats globalParsedStats = globalStatsParser.parse();
        int noServicePct = globalParsedStats.getNoServicePct();
        int returnCode;
        String msg;
        if (noServicePct > 0) {
            msg = "Passive tracking is failing for " + noServicePct + "% of drones";
            returnCode = 1;
        } else {
            msg = "Everything is awesome";
            returnCode = 0;
        }
        System.out.println("\n" + msg);
        exit(returnCode);
    }
}
