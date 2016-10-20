package com.precisionhawk.latas.client.util;

import java.util.Properties;

public class ConnectionProperties {

    private final Properties properties;

    public ConnectionProperties(Properties properties) {
        this.properties = properties;
    }

    public String getServiceHost() {
        return properties.getProperty("serviceHost");
    }

    public String getProtocol() {
        return isSsl() ? "https://" : "http://";
    }

    public boolean isSsl() {
        return Boolean.parseBoolean(properties.getProperty("ssl"));
    }

    public String getClientId() {
        return properties.getProperty("clientId");
    }

    public String getSecret() {
        return properties.getProperty("secret");
    }

    public String getMqttHost() {
        return properties.getProperty("mqttHost");
    }
}
