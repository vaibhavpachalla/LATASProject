package com.precisionhawk.latas.client.stats;

import com.precisionhawk.latas.client.util.ExceptionUtil;
import com.precisionhawk.latas.client.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.Executors;

public class StatsServer {

    private static NumberFormat numberFormat = NumberFormat.getInstance();

    static {
        numberFormat.setMaximumFractionDigits(1);
    }

    private static HttpServer server;

    public static void main(String[] args) {
        StatsServer.start();
    }

    public static void start() {
        server = ExceptionUtil.duckSupplier(() ->
            HttpServer.create(new InetSocketAddress(11111), 0)
        );

        // run the server in a separate thread, not this one
        server.setExecutor(Executors.newSingleThreadExecutor());

        server.createContext("/", new RootHandler());
        server.createContext("/drones/", new DroneHandler());
        server.start();
    }

    public static void stop() {
        server.stop(0);
    }

    private static void print(HttpExchange x, String response) throws IOException {
        x.sendResponseHeaders(200, response.length());
        x.setAttribute("content-type", "text/html");
        OutputStream os = x.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange x) throws IOException {
            GlobalStatsParser globalStatsParser = new GlobalStatsParser(GlobalRawStats.getInstance().getAllDroneStats());
            GlobalParsedStats globalParsedStats = globalStatsParser.parse();
            StringBuilder sb = new StringBuilder();
            // Using a template engine would be probably be overkill at the moment
            sb.append("<html>");
            sb.append("<head>");
            sb.append("<style>");
            sb.append("th, td { border: solid black 1px; text-align: right; padding: 6px; } .lag { background-color: #ee6666; }");
            sb.append("</style>");
            sb.append("</head>");
            sb.append("<dl>");
            double globalAvgMqttLatency = globalParsedStats.getGlobalAvgMqttLatency();
            sb.append("<dt>Global Mqtt Latency</dt><dd>").append(numberFormat.format(globalAvgMqttLatency)).append(" ms</dd>");
            sb.append("<dt>Global Stomp Latency</dt><dd>").append(numberFormat.format(globalParsedStats.getGlobalAvgStompLatency())).append(" ms</dd>");
            sb.append("<dt>Drones with no service</dt><dd>").append(globalParsedStats.getNoServicePct()).append("%</dd>");
            sb.append("</dl>");
            sb.append("<table style='border-collapse: collapse;'><tr><th>Drone Id</th><th>STOMP Lag (msgs)</th><th>STOMP Lag (pct.)</th></tr>");
            List<DroneParsedStats> droneParsedStatsList = globalParsedStats.getDroneParsedStatsList();
            for (DroneParsedStats p : droneParsedStatsList) {
                int droneId = p.getDroneRawStats().getDroneId();
                int stompLagPct = p.getStompLagPct();
                sb.append("<tr ").append(stompLagPct == 100 ? "class='lag'" : "").append(">");
                sb.append("<td><a href='/drones/").append(droneId).append("'>").append(droneId).append("</a></td>");
                sb.append("<td>").append(p.getStompLagCount()).append("</td>");
                sb.append("<td>").append(stompLagPct).append("%</td>");
                sb.append("</tr>");
            }
            sb.append("</table></html>");
            print(x, sb.toString());
        }
    }

    private static class DroneHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            URI requestURI = httpExchange.getRequestURI();
            String path = requestURI.getPath();
            String droneId = path.substring(8);
            GlobalRawStats globalRawStats = GlobalRawStats.getInstance();
            DroneRawStats droneRawStats = globalRawStats.getDroneStats(Integer.parseInt(droneId));
            DroneStatsParser droneStatsParser = new DroneStatsParser(droneRawStats);
            DroneParsedStats droneParsedStats = droneStatsParser.parse();
            StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            sb.append("<!--").append(JsonUtil.toJson(droneParsedStats)).append("-->");
            // Vintage HTML from 1995
            sb.append("<table border='1'><tr><th>Mqtt Sent</th><th>Stomp Received</th></tr>");
            List<DroneTelemetryRoundTrip> roundTrips = droneParsedStats.getRoundTrips();
            for (DroneTelemetryRoundTrip roundTrip : roundTrips) {
                sb.append("<tr>");
                sb.append("<td><pre>").append(JsonUtil.toJson(roundTrip.getDronePacketStats())).append("</pre></td>");
                sb.append("<td><pre>").append(JsonUtil.toJson(roundTrip.getStompStats())).append("</pre></td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
            sb.append("</html>");
            print(httpExchange, sb.toString());
        }
    }
}
