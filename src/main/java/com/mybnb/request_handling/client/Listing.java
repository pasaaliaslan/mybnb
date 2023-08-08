package com.mybnb.request_handling.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.DAO.GetHandler;
import com.mybnb.Http;
import com.mybnb.SQLStatusExceptions.BaseSQLStatusException;
import com.mybnb.request_handling.HandlerResponse;
import com.mybnb.request_handling.Viewset;

public class Listing extends Viewset {

    public Listing(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();

                // LinkedHashMap preserves insertion order
                // which is useful if user wants to order results by field
                // as sorting happens in the database level.
                LinkedHashMap<Integer, Map<String, Object>> listings = new LinkedHashMap<>();

                while (rs.next()) {
                    Integer listingId = rs.getInt("listingId");

                    if (!listings.containsKey(listingId)) {
                        Map<String, Object> listing = new HashMap<>();

                        listing.put("listingId", listingId);
                        listing.put("residenceId", rs.getInt("residenceId"));
                        listing.put("description", rs.getString("description"));
                        listing.put("pricePerNight", rs.getInt("pricePerNight"));
                        listing.put("residenceType", rs.getString("residenceTypeName"));
                        listing.put("hostUsername", rs.getString("hostUsername"));
                        listing.put("numberOfBedrooms", rs.getInt("numberOfBedrooms"));
                        listing.put("numberOfBeds", rs.getInt("numberOfBeds"));
                        listing.put("numberOfBaths", rs.getInt("numberOfBaths"));
                        listing.put("roomNumber", rs.getInt("roomNumber"));
                        listing.put("longitude", rs.getDouble("longitude"));
                        listing.put("latitude", rs.getDouble("latitude"));
                        listing.put("streetName", rs.getString("streetName"));
                        listing.put("postalCode", rs.getString("postalCode"));
                        listing.put("cityName", rs.getString("cityName"));
                        listing.put("subcountryName", rs.getString("subcountryName"));
                        listing.put("countryName", rs.getString("countryName"));
                        listing.put("amenities", new ArrayList<String>());

                        String amenity = rs.getString("amenityName");

                        if (amenity != null) {
                            ((ArrayList<String>) listing.get("amenities")).add(amenity);
                        }

                        listings.put(listingId, listing);
                    }

                    else {
                        ((ArrayList<String>) listings.get(listingId).get("amenities")).add(rs.getString("amenityName"));
                    }
                }

                response.put("listings", listings.values());
                return response;
            }
        }

        JSONObject responseBody;

        String sqlQuery = ClientHelpers.buildListingGetQuery(requestBody);

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

        // Try creating Residence.
        String residenceFields[] = { "residenceTypeName", "hostUsername", "numberOfBedrooms", "numberOfBeds",
                "numberOfBaths", "longitude", "latitude" };
        try {
            for (String field : residenceFields) {
                mapping.put(field, requestBody.get(field));
            }

            if (requestBody.has("roomNumber")) {
                mapping.put("roomNumber", requestBody.getString("roomNumber"));
            }
        } catch (JSONException e) {
            return Http.MESSAGE_RESPONSE("Missing residence information", Http.STATUS.BAD_REQUEST);
        }

        int residenceId;

        try {
            residenceId = dao.create(mapping, "Residence", true);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        // Try creating listing.
        mapping.clear();
        mapping.put("residenceId", residenceId);

        String listingFields[] = { "description", "pricePerNight", "availabilityStart", "availabilityEnd" };
        try {
            for (String field : listingFields) {
                mapping.put(field, requestBody.get(field));
            }

        } catch (JSONException e) {
            return Http.MESSAGE_RESPONSE("Missing listing information", Http.STATUS.BAD_REQUEST);
        }

        try {
            dao.create(mapping, "Listing", false);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.MESSAGE_RESPONSE("Listing added successfully.", Http.STATUS.OK);
    }

    @Override
    public HandlerResponse update(JSONObject queryParams, JSONObject requestBody) {
        HashMap<String, Object> newMapping = new HashMap<String, Object>();
        HashMap<String, Object> keyConditions = new HashMap<String, Object>();

        if (!requestBody.has("listingId")) {
            return Http.MESSAGE_RESPONSE("Missing listing id", Http.STATUS.BAD_REQUEST);
        }

        keyConditions.put("listingId", requestBody.getString("listingId"));

        String listingFields[] = { "pricePerNight", "availabilityStart", "availabilityEnd" };

        for (String field : listingFields) {
            if (requestBody.has(field)) {
                newMapping.put(field, requestBody.get(field));
            }
        }

        try {
            dao.update(newMapping, keyConditions, "Listing", false);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.MESSAGE_RESPONSE("Updated listing successfully.", Http.STATUS.OK);
    }

    @Override
    public HandlerResponse delete(JSONObject queryParams, JSONObject requestBody) {
        HashMap<String, Object> newMapping = new HashMap<String, Object>();
        HashMap<String, Object> keyConditions = new HashMap<String, Object>();

        if (!requestBody.has("listingId")) {
            return Http.MESSAGE_RESPONSE("Missing listing id", Http.STATUS.BAD_REQUEST);
        }

        keyConditions.put("listingId", requestBody.getString("listingId"));
        newMapping.put("isAvailable", false);

        try {
            dao.update(newMapping, keyConditions, "Listing", false);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.MESSAGE_RESPONSE("Updated listing successfully.", Http.STATUS.OK);
    }

}
