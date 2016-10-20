package com.precisionhawk.latas.client.net;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.precisionhawk.latas.client.util.ConnectionProperties;
import com.precisionhawk.latas.client.util.ExceptionUtil;
import com.precisionhawk.latas.client.util.JsonUtil;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.util.function.Consumer;

/**
 * Encapsulates an oauth token so you can make API calls without having to pass the token around.
 */
public class LatasSession {

    static {
        Unirest.setObjectMapper(JsonUtil.getObjectMapper());
    }

    private final ConnectionProperties connectionProperties;
    private final String baseUrl;
    private final Scope scope;
    private String token;

    public enum Scope {
        DEVICE, ORGANIZATION_ADMIN
    }

    public LatasSession(ConnectionProperties connectionProperties, Scope scope) {
        this.connectionProperties = connectionProperties;
        baseUrl = connectionProperties.getProtocol() + connectionProperties.getServiceHost();
        this.scope = scope;
    }

    public void loadOauthToken() {
        String auth = connectionProperties.getClientId() + ':' + connectionProperties.getSecret();
        String b64 = DatatypeConverter.printBase64Binary(auth.getBytes());
        String authHeaderValue = "Basic " + b64;
        GetRequest getRequest = Unirest.get(
            baseUrl + "/oauth/token?grant_type=client_credentials&scope=" + scope
        ).header(
            "Authorization", authHeaderValue
        ).header(
            "Accept", "application/json"
        ).header(
            "Accept-Version", "1.0"
        ).header(
            "Content-Type", "application/json"
        );

        System.out.println("loadOauthToken...");
        HttpResponse<JsonNode> httpResponse = ExceptionUtil.duckSupplier(getRequest::asJson);
        System.out.println("httpResponse = " + httpResponse.getBody());
        token = httpResponse.getBody().getObject().getString("access_token");
    }

    public JSONObject httpGet(String path) {
        return processRequest(Unirest.get(baseUrl + path));
    }

    public JSONObject httpPost(String path, Consumer<HttpRequestWithBody> reqConsumer) {
        HttpRequestWithBody req = Unirest.post(baseUrl + path);
        reqConsumer.accept(req);
        return processRequest(req);
    }

    private JSONObject processRequest(HttpRequest req) {
        req.header(
            "Authorization", "Bearer " + token
        ).header(
            "Content-Type", "application/x-www-form-urlencoded"
        );
        HttpResponse<JsonNode> httpResponse = ExceptionUtil.duckSupplier(req::asJson);
        return httpResponse.getBody().getObject();
    }

    public DroneMqttPublisher getDronePublisher(int id) {
        return new DroneMqttPublisher(id, this);
    }

    public boolean isSecure() {
        return connectionProperties.isSsl();
    }

    String getToken() {
        return token;
    }
}
