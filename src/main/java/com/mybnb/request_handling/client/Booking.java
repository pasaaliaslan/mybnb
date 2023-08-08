package com.mybnb.request_handling.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.DAO.GetHandler;
import com.mybnb.Http;
import com.mybnb.SQLStatusExceptions.BaseSQLStatusException;
import com.mybnb.request_handling.HandlerResponse;
import com.mybnb.request_handling.Viewset;

public class Booking extends Viewset {

    public Booking(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();
                List<JSONObject> bookings = new ArrayList<>();

                while (rs.next()) {
                    JSONObject booking = new JSONObject();
                    booking.put("id", rs.getInt("id"));
                    booking.put("startDate", rs.getDate("startDate"));
                    booking.put("endDate", rs.getDate("endDate"));
                    booking.put("renterUsername", rs.getString("renterUsername"));
                    booking.put("listingId", rs.getInt("listingId"));
                    booking.put("pricePerNight", rs.getDouble("pricePerNight"));
                }

                response.put("bookings", bookings);
                return response;
            }
        }

        JSONObject responseBody;

        String sqlQuery;
        String showBy = "";

        if (queryParams.has("showBy")) {
            switch (queryParams.getString("showBy")) {
                case "NOT CANCELLED":
                    showBy = "AND cancelledBy='NONE'";
                    break;
                case "CANCELLED":
                    showBy = "AND cancelledBy IN ('RENTER', 'HOST')";
                    break;
            }
        }

        if (queryParams.has("renterUsername")) {
            String renterUsername = queryParams.getString("renterUsername");
            sqlQuery = String.format("SELECT * FROM Booking WHERE renterUsername=%s %s", renterUsername, showBy);
        } else if (queryParams.has("hostUsername")) {
            String hostUsername = queryParams.getString("hostUsername");
            sqlQuery = String.join(" ",
                    "SELECT Booking.id AS id, startDate, endDate, renterUsername, listingId, pricePerNight",
                    "FROM Booking JOIN Listing ON Booking.listingId=Listing.id",
                    String.format("WHERE hostUsername=%s %s", hostUsername, showBy));

        } else {
            return Http.MESSAGE_RESPONSE("A renter or a host username must be provided.", Http.STATUS.BAD_REQUEST);
        }

        try {
            responseBody = this.dao.get(sqlQuery, new DaoFn());
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }

    @Override
    public HandlerResponse create(JSONObject queryParams, JSONObject requestBody) {
        HashMap<String, Object> mapping = new HashMap<String, Object>();

        String listingFields[] = { "renterUsername", "listingId", "startDate", "endDate" };
        try {
            for (String field : listingFields) {
                mapping.put(field, requestBody.get(field));
            }

        } catch (JSONException e) {
            return Http.MESSAGE_RESPONSE("Missing booking information", Http.STATUS.BAD_REQUEST);
        }

        try {
            dao.create(mapping, "Booking", false);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.MESSAGE_RESPONSE("Listing is booked successfully.", Http.STATUS.OK);
    }

    @Override
    public HandlerResponse delete(JSONObject queryParams, JSONObject requestBody) {
        HashMap<String, Object> newMapping = new HashMap<String, Object>();
        HashMap<String, Object> keyConditions = new HashMap<String, Object>();

        if (!requestBody.has("bookingId")) {
            return Http.MESSAGE_RESPONSE("Missing booking id", Http.STATUS.BAD_REQUEST);
        }

        if (!requestBody.has("cancelledBy")) {
            return Http.MESSAGE_RESPONSE("No responsible", Http.STATUS.BAD_REQUEST);
        }

        keyConditions.put("bookingId", requestBody.getString("bookingId"));
        newMapping.put("cancelledBy", requestBody.getString("cancelledBy"));

        try {
            dao.update(newMapping, keyConditions, "Booking", false);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.MESSAGE_RESPONSE("Cancelled booking.", Http.STATUS.OK);
    }

}
