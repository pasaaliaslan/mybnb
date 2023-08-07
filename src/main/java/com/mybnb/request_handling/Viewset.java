package com.mybnb.request_handling;

import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.Http;

public abstract class Viewset {
    public DAO dao;

    public Viewset(DAO dao) {
        this.dao = dao;
    }

    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        return Http.UNSUPPORTED_METHOD_RESPONSE;
    }

    public HandlerResponse create(JSONObject queryParams, JSONObject requestBody) {
        return Http.UNSUPPORTED_METHOD_RESPONSE;
    }

    public HandlerResponse update(JSONObject queryParams, JSONObject requestBody) {
        return Http.UNSUPPORTED_METHOD_RESPONSE;
    }

    public HandlerResponse delete(JSONObject queryParams, JSONObject requestBody) {
        return Http.UNSUPPORTED_METHOD_RESPONSE;
    }
}
