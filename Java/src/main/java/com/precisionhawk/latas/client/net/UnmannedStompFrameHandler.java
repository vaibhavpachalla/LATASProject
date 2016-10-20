package com.precisionhawk.latas.client.net;

import com.precisionhawk.latas.client.util.JsonUtil;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.function.Consumer;

class UnmannedStompFrameHandler implements StompFrameHandler {

    private final Consumer<StompResponse> responseConsumer;

    UnmannedStompFrameHandler(Consumer<StompResponse> responseConsumer) {
        this.responseConsumer = responseConsumer;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        StompResponse stompResponse = JsonUtil.fromJson((String) payload, StompResponse.class);
        responseConsumer.accept(stompResponse);
    }
}
