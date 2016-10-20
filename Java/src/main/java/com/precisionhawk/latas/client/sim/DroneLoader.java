package com.precisionhawk.latas.client.sim;

import com.precisionhawk.latas.client.net.LatasSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DroneLoader {

    private static final int PAGESIZE = 100;

    private final LatasSession apiSession;

    public DroneLoader(LatasSession apiSession) {
        this.apiSession = apiSession;
    }

    public List<JSONObject> load(int max) {
        int pages = ((max - 1) / PAGESIZE) + 1;
        List<JSONObject> out = new ArrayList<>();
        for (int i = 0; i < pages; i++) {
            JSONArray drones = getPage(i);
            for (Object o : drones) {
                JSONObject jsonDrone = (JSONObject) o;
                out.add(jsonDrone);
                if (out.size() >= max) break;
            }
        }
        return out;
    }

    private JSONArray getPage(int page) {
        String path = "/api/drones?page=" + (page + 1) + "&size=" + PAGESIZE;
        JSONObject response = apiSession.httpGet(path);
        return response.getJSONArray("drones");
    }
}
