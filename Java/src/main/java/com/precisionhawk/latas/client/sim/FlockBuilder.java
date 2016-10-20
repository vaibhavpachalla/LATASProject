package com.precisionhawk.latas.client.sim;

import com.precisionhawk.latas.client.model.DroneProperties;
import com.precisionhawk.latas.client.model.Flock;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FlockBuilder {

    private final Flock flock;
    private final List<JSONObject> jsonDrones;
    private final int side;

    public FlockBuilder(Flock flock, List<JSONObject> jsonDrones) {
        this.flock = flock;
        this.jsonDrones = jsonDrones;
        double sqrt = Math.sqrt(jsonDrones.size());
        side = (int) Math.ceil(sqrt);
    }

    public List<DroneProperties> getDrones() {
        List<DroneProperties> out = new ArrayList<>();
        for (int i = 0; i < jsonDrones.size(); i++) {
            out.add(setUpDrone(i));
        }
        return out;
    }

    private DroneProperties setUpDrone(int i) {
        JSONObject jsonDrone = jsonDrones.get(i);
        DroneProperties droneProperties = new DroneProperties(flock);
        droneProperties.setId(jsonDrone.getInt("droneId"));
        double dx = (i % side) * flock.getSeparation();
        double dy = (i / side) * flock.getSeparation();
        droneProperties.setLon(flock.getLon() + dx);
        droneProperties.setLat(flock.getLat() - dy);
        return droneProperties;
    }
}
