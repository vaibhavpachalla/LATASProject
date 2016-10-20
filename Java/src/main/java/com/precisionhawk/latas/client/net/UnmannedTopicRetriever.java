package com.precisionhawk.latas.client.net;

import com.precisionhawk.latas.client.model.ViewPort;
import org.json.JSONObject;

class UnmannedTopicRetriever {

    private final ViewPort viewPort;

    UnmannedTopicRetriever(ViewPort viewPort) {
        this.viewPort = viewPort;
    }

    String getUnmannedTopic(LatasSession latasSession) {
        JSONObject unmannedResp = latasSession.httpPost(
            "/api/streaming/topic/unmanned",
            req -> req.field(
                "topRightCoord", viewPort.getTopRight().toString()
            ).field(
                "bottomLeftCoord", viewPort.getBottomLeft().toString()
            )
        );
        return unmannedResp.getString("subscriptionTopic");
    }
}
