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
        try {
            mapping.put("residenceTypeName", requestBody.getString("residenceTypeName"));
            mapping.put("hostUsername", requestBody.getString("hostUsername"));
            mapping.put("numberOfBedrooms", requestBody.getInt("numberOfBedrooms"));
            mapping.put("numberOfBeds", requestBody.getString("numberOfBeds"));
            mapping.put("numberOfBaths", requestBody.getString("numberOfBaths"));
            mapping.put("longitude", requestBody.getString("longitude"));
            mapping.put("latitude", requestBody.getString("latitude"));

            if (requestBody.has("roomNumber")) {
                mapping.put("roomNumber", requestBody.getString("roomNumber"));
            }
        } catch (JSONException e) {
            return Http.MESSAGE_RESPONSE("Missing residence information", Http.STATUS.BAD_REQUEST);
        }

        try {
            dao.create(mapping, "Residence");
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        // Try creating listing.

        return Http.MESSAGE_RESPONSE("Created Address successfully.", Http.STATUS.OK);
    }

    @Override
    public HandlerResponse update(JSONObject queryParams, JSONObject requestBody) {
        JSONObject responseBody = null;
        responseBody = new JSONObject().put("listing", "update");

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }

    @Override
    public HandlerResponse delete(JSONObject queryParams, JSONObject requestBody) {
        JSONObject responseBody = null;
        responseBody = new JSONObject().put("listing", "delete");

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }

}
