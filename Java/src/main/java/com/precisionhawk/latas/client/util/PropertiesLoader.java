package com.precisionhawk.latas.client.util;

import com.precisionhawk.latas.client.model.DroneProperties;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader {

    private static final String CONNECTION = "connection";

    private final Map<String, Properties> map = new HashMap<>();
    private final String env;

    public PropertiesLoader(String env) {
        this.env = env;
        loadProperties(CONNECTION);
    }

    public ConnectionProperties getConnectionProperties() {
        Properties properties = getProperties(CONNECTION);
        return new ConnectionProperties(properties);
    }

    public DroneProperties getDroneProperties(String name) {
        Properties properties = getProperties(name);
        DroneProperties out = new DroneProperties();
        DroneUtil.populateDroneProperties(out, properties);
        return out;
    }

    public Properties getProperties(String name) {
        Properties properties = map.get(name);
        if (properties == null) {
            properties = loadProperties(name);
            map.put(name, properties);
        }
        return properties;
    }

    private Properties loadProperties(String name) {
        String path = env + '/' + name + ".properties";
        InputStream resourceAsStream = ClassLoader.getSystemResourceAsStream(path);
        if (resourceAsStream == null) {
            System.out.println("resource not found: " + path);
            System.exit(1);
        }
        Properties properties = new Properties();
        ExceptionUtil.duckRunnable(() ->
            properties.load(resourceAsStream)
        );
        return properties;
    }
}
