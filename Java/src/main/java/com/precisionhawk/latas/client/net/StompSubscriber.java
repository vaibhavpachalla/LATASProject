package com.precisionhawk.latas.client.net;

import com.precisionhawk.latas.client.model.ViewPort;
import com.precisionhawk.latas.client.util.ExceptionUtil;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Collections;
import java.util.function.Consumer;

public class StompSubscriber {

    private final LatasSession latasSession;
    private final String serviceHost;
    private final StompHeaders stompHeaders;

    public StompSubscriber(LatasSession latasSession, String serviceHost) {
        this.latasSession = latasSession;
        this.serviceHost = serviceHost;
        stompHeaders = new StompHeaders();
        stompHeaders.put("token", Collections.singletonList(latasSession.getToken()));
    }

    private static WebSocketStompClient getWebSocketStompClient() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());
        return stompClient;
    }

    public void subscribe(ViewPort viewPort, Consumer<StompResponse> responseConsumer) {
        String protocol = latasSession.isSecure() ? "wss://" : "ws://";
        String url = protocol + serviceHost + "/unmanned";
        ListenableFuture<StompSession> sessionFuture = getWebSocketStompClient().connect(
            url,
            new WebSocketHttpHeaders(),
            stompHeaders,
            new StompSessionHandlerAdapter() {
                @Override public void afterConnected(StompSession session, StompHeaders connectedHeaders) {}
            }
        );
        StompSession stompSession = ExceptionUtil.duckSupplier(sessionFuture::get);
        String subscriptionTopic = getUnmannedTopic(viewPort);
        stompSession.subscribe(subscriptionTopic, new UnmannedStompFrameHandler(responseConsumer));
    }

    private String getUnmannedTopic(ViewPort viewPort) {
        UnmannedTopicRetriever unmannedTopicRetriever = new UnmannedTopicRetriever(viewPort);
        return unmannedTopicRetriever.getUnmannedTopic(latasSession);
    }
}
