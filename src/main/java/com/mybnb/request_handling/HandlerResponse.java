package com.mybnb.request_handling;

import org.json.JSONObject;

public class HandlerResponse {
    private String responseBody;
    private int responseStatus;

    public HandlerResponse(JSONObject responseBody, int responseStatus) {
        this.responseBody = responseBody.toString();
        this.responseStatus = responseStatus;
    }

    public int getResponseStatus() {
        return this.responseStatus;
    }

    public String getResponseBody() {
        return this.responseBody;
    }
}
