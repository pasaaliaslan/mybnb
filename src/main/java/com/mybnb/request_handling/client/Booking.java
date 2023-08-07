package com.mybnb.request_handling.client;

import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.Http;
import com.mybnb.request_handling.HandlerResponse;
import com.mybnb.request_handling.Viewset;

public class Booking extends Viewset {

    public Booking(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        JSONObject responseBody = null;
        responseBody = new JSONObject().put("booking", "get");

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }

    @Override
    public HandlerResponse create(JSONObject queryParams, JSONObject requestBody) {
        JSONObject responseBody = null;
        responseBody = new JSONObject().put("booking", "create");

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }

    @Override
    public HandlerResponse update(JSONObject queryParams, JSONObject requestBody) {
        JSONObject responseBody = null;
        responseBody = new JSONObject().put("booking", "update");

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }

    @Override
    public HandlerResponse delete(JSONObject queryParams, JSONObject requestBody) {
        JSONObject responseBody = null;
        responseBody = new JSONObject().put("booking", "delete");

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }

}
