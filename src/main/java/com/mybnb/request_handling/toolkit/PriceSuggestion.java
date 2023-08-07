package com.mybnb.request_handling.toolkit;

import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.Http;
import com.mybnb.request_handling.HandlerResponse;
import com.mybnb.request_handling.Viewset;

public class PriceSuggestion extends Viewset {

    public PriceSuggestion(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        JSONObject responseBody = null;
        responseBody = new JSONObject().put("price_suggestion", "get");

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }
}
