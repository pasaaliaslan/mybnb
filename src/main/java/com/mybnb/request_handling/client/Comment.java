package com.mybnb.request_handling.client;

import java.util.HashMap;

import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.Http;
import com.mybnb.SQLStatusExceptions.BaseSQLStatusException;
import com.mybnb.request_handling.HandlerResponse;
import com.mybnb.request_handling.Viewset;

public class Comment extends Viewset {

    public Comment(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse create(JSONObject queryParams, JSONObject requestBody) {
        HashMap<String, Object> mapping = new HashMap<String, Object>();

        mapping.put("bookingId", requestBody.getInt("bookingId"));
        mapping.put("comment", requestBody.getString("comment"));
        mapping.put("rating", requestBody.getInt("rating"));

        String tableName = String.format("Review%s", requestBody.getString("commentFor"));

        try {
            this.dao.create(mapping, tableName, false);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.MESSAGE_RESPONSE("Comment is  made.", Http.STATUS.OK);
    }
}
