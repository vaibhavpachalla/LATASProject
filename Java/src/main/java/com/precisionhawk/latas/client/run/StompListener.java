package com.precisionhawk.latas.client.run;

import com.beust.jcommander.Parameter;
import com.precisionhawk.latas.client.model.ViewPort;
import com.precisionhawk.latas.client.net.LatasSession;
import com.precisionhawk.latas.client.net.StompSubscriber;
import com.precisionhawk.latas.client.util.ConnectionProperties;
import com.precisionhawk.latas.client.util.ExceptionUtil;
import com.precisionhawk.latas.client.util.PropertiesLoader;
import com.precisionhawk.latas.client.util.ViewPortExtent;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

public class StompListener implements Runnable {

    @Parameter
    private List<String> args = new ArrayList<>();

    public void run() {
        if (args.size() != 5) {
            printUsage();
        }

        String env = args.get(1);
        PropertiesLoader propertiesLoader = new PropertiesLoader(env);
        ConnectionProperties connectionProperties = propertiesLoader.getConnectionProperties();

        LatasSession deviceSession = new LatasSession(connectionProperties, LatasSession.Scope.ORGANIZATION_ADMIN);
        deviceSession.loadOauthToken();

        StompSubscriber stompSubscriber = new StompSubscriber(
            deviceSession,
            connectionProperties.getServiceHost()
        );

        ViewPort viewPort = getViewPort(args);

        stompSubscriber.subscribe(
            viewPort,
            stompResponse -> System.out.println("stompResponse = " + stompResponse)
        );
        hang();
    }

    private static ViewPort getViewPort(List<String> args) {
        double lat = parseDouble(args.get(2));
        double lon = parseDouble(args.get(3));
        double padding = parseDouble(args.get(4));

        ViewPortExtent viewPortExtent = new ViewPortExtent();
        viewPortExtent.add(lat, lon);
        return viewPortExtent.getViewPortWithPadding(padding);
    }

    private synchronized void hang() {
        // this just waits for the user to exit via e.g. ctrl-c
        ExceptionUtil.duckRunnable(this::wait);
    }

    private static void printUsage() {
        System.out.println("Usage: args: stomp env lat lon padding");
        System.out.println("where lat, lon describe the center of the viewport and padding is the viewport 'radius' in degrees");
        System.out.println("e.g. stomp prod 35.9 -78.61 0.5");
        System.exit(0);
    }
}
