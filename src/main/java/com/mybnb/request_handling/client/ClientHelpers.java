package com.mybnb.request_handling.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.Http;
import com.mybnb.SQLStatusExceptions.BaseSQLStatusException;
import com.mybnb.SQLStatusExceptions.ForeignKeyFailException;
import com.mybnb.SQLStatusExceptions.UnknownSQLException;
import com.mybnb.request_handling.HandlerResponse;

public class ClientHelpers {

    public static HandlerResponse createUserFromRequest(DAO dao, JSONObject requestBody) {
        HashMap<String, Object> mapping = new HashMap<String, Object>();

        String username = requestBody.getString("username");

        // Try creating row in User.
        try {
            mapping.put("username", username);
            mapping.put("password", requestBody.getString("password"));
            mapping.put("insuranceNumber", requestBody.getInt("insuranceNumber"));
            mapping.put("firstName", requestBody.getString("firstName"));
            mapping.put("lastName", requestBody.getString("lastName"));
            mapping.put("dateOfBirth", requestBody.getString("dateOfBirth"));
            mapping.put("occupation", requestBody.getString("occupation"));
        } catch (JSONException e) {
            return Http.MESSAGE_RESPONSE("Missing user information", Http.STATUS.BAD_REQUEST);
        }

        try {
            dao.create(mapping, "User", false);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        // Create Address for the user.
        HandlerResponse addressResponse = createAddressForUserFromRequest(dao, requestBody);

        if (!Http.isSuccessResponse(addressResponse)) {
            return addressResponse;
        }

        return Http.MESSAGE_RESPONSE("Created Address successfully.", Http.STATUS.OK);
    }

    private static HandlerResponse createAddressForUserFromRequest(DAO dao, JSONObject requestBody) {

        HandlerResponse addressCreation = createAddress(dao, requestBody);

        if (!Http.isSuccessResponse(addressCreation)) {
            return addressCreation;
        }

        HashMap<String, Object> mapping = new HashMap<String, Object>();

        // Try creating row in livesIn.
        JSONObject address = requestBody.getJSONObject("address");

        mapping.put("postalCode", address.getString("postalCode"));
        mapping.put("streetName", address.getString("streetName"));
        mapping.put("cityName", address.getString("city"));
        mapping.put("subcountryName", address.getString("subcountry"));
        mapping.put("countryName", address.getString("country"));
        mapping.put("username", requestBody.getString("username"));

        try {
            dao.create(mapping, "livesIn", false);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.MESSAGE_RESPONSE("Created Address successfully.", Http.STATUS.OK);
    }

    public static HandlerResponse updateUserFromRequest(DAO dao, JSONObject requestBody) {
        HashMap<String, Object> newMapping = new HashMap<String, Object>();
        HashMap<String, Object> keyConditions = new HashMap<String, Object>();

        if (!requestBody.has("username")) {
            return Http.MESSAGE_RESPONSE("Missing username", Http.STATUS.BAD_REQUEST);
        }

        keyConditions.put("username", requestBody.getString("username"));

        String fields[] = { "insuranceNumber", "firstName", "lastName", "dateOfBirth", "occupation" };

        for (String field : fields) {
            if (requestBody.has(field)) {
                newMapping.put(field, requestBody.get(field));
            }
        }

        try {
            dao.update(newMapping, keyConditions, "User", false);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        if (requestBody.has("address")) {
            HandlerResponse addressUpdate = updateAddressForUserFromRequest(dao, requestBody);

            if (!Http.isSuccessResponse(addressUpdate)) {
                return addressUpdate;
            }
        }

        return Http.MESSAGE_RESPONSE("Updated user successfully.", Http.STATUS.OK);
    }

    private static HandlerResponse updateAddressForUserFromRequest(DAO dao, JSONObject requestBody) {
        HashMap<String, Object> mapping = new HashMap<String, Object>();
        HashMap<String, Object> keyConditions = new HashMap<String, Object>();

        try {
            keyConditions.put("username", requestBody.getString("username"));
        } catch (JSONException e) {
            return Http.MESSAGE_RESPONSE("Missing username", Http.STATUS.BAD_REQUEST);
        }

        HandlerResponse addressCreation = createAddress(dao, requestBody);

        if (!Http.isSuccessResponse(addressCreation)) {
            return addressCreation;
        }

        JSONObject address = requestBody.getJSONObject("address");

        mapping.put("postalCode", address.getString("postalCode"));
        mapping.put("streetName", address.getString("streetName"));
        mapping.put("cityName", address.getString("city"));
        mapping.put("subcountryName", address.getString("subcountry"));
        mapping.put("countryName", address.getString("country"));

        // Try updating row in livesIn.
        try {
            dao.update(mapping, keyConditions, "livesIn", false);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.MESSAGE_RESPONSE("Update Address successfully.", Http.STATUS.OK);
    }

    private static HandlerResponse createAddress(DAO dao, JSONObject requestBody) {
        HashMap<String, Object> mapping = new HashMap<String, Object>();

        JSONObject address;

        // Try creating row in PostalCode.
        try {
            address = requestBody.getJSONObject("address");
            mapping.put("code", address.getString("postalCode"));
            mapping.put("cityName", address.getString("city"));
            mapping.put("subcountryName", address.getString("subcountry"));
            mapping.put("countryName", address.getString("country"));
        } catch (JSONException e) {
            return Http.MESSAGE_RESPONSE("Missing address information", Http.STATUS.BAD_REQUEST);
        }

        try {
            dao.create(mapping, "PostalCode", false);
        } catch (ForeignKeyFailException | UnknownSQLException e) {
            e.getHttpResponse();
        } catch (BaseSQLStatusException e) {
        }

        // Try creating row in Address.
        try {
            mapping.remove("code");
            mapping.put("postalCode", address.getString("postalCode"));
            mapping.put("streetName", address.getString("streetName"));
        } catch (JSONException e) {
            return Http.MESSAGE_RESPONSE("Missing user information", Http.STATUS.BAD_REQUEST);
        }

        try {
            dao.create(mapping, "Address", false);
        } catch (UnknownSQLException e) {
            return e.getHttpResponse();
        } catch (BaseSQLStatusException e) {
        }

        return Http.MESSAGE_RESPONSE("Created Address", Http.STATUS.OK);
    }

    // public static HandlerResponse createLocation(DAO dao, JSONObject requestBody)
    // {
    // HashMap<String, Object> mapping = new HashMap<String, Object>();

    // JSONObject location;

    // try {
    // location = requestBody.getJSONObject("location");
    // mapping.put("longitude", location.getDouble("longitude"));
    // mapping.put("latitude", location.getDouble("latitude"));
    // mapping.put("postalCode", location.getString("postalCode"));
    // mapping.put("cityName", location.getString("city"));
    // mapping.put("subcountryName", location.getString("subcountry"));
    // mapping.put("countryName", location.getString("country"));
    // } catch (JSONException e) {
    // return Http.MESSAGE_RESPONSE("Missing location information",
    // Http.STATUS.BAD_REQUEST);
    // }

    // }

    public static String buildListingGetQuery(JSONObject requestBody) {
        String[] fieldsAsList = { "Listing.id AS listingId", "Residence.id AS residenceId", "description",
                "pricePerNight", "residenceTypeName", "hostUsername", "numberOfBedrooms", "numberOfBeds",
                "numberOfBaths", "roomNumber", "Location.longitude AS longitude", "Location.latitude AS latitude",
                "availabilityStart", "availabilityEnd", "streetName", "postalCode", "cityName",
                "subcountryName", "countryName", "amenityName" };
        String fieldsAsString = String.join(", ", fieldsAsList);

        String sqlQuery = String.join(" ",
                "SELECT",
                fieldsAsString,
                "FROM Listing",
                "JOIN Residence ON Residence.id=Listing.residenceId",
                "JOIN Location ON Residence.longitude=Location.longitude AND Residence.latitude=Location.latitude",
                "LEFT JOIN includes ON includes.residenceId=Residence.id");

        String filterString = getListingFilterString(requestBody);

        if (!filterString.isEmpty()) {
            sqlQuery = String.format("%s WHERE %s", sqlQuery, filterString);
        }

        sqlQuery = String.format("%s %s", sqlQuery, getListingOrderString(requestBody));

        return sqlQuery;
    }

    private static String getListingOrderString(JSONObject requestBody) {
        if (!requestBody.has("order")) {
            return "";
        }

        JSONObject order = requestBody.getJSONObject("order");

        String field = order.getString("field");
        boolean isDescending = order.getBoolean("isDescending");

        return String.format("ORDER BY %s %s", field, isDescending ? "DESC" : "ASC");
    }

    private static String getListingFilterString(JSONObject requestBody) {
        List<String> filters = new ArrayList<String>();

        if (requestBody.has("region")) {
            JSONObject region = requestBody.getJSONObject("region");

            double centerLongitude = region.getDouble("centerLongitude");
            double centerLatitude = region.getDouble("centerLatitude");
            double radiusInKm = 10;

            if (region.has("radius")) {
                radiusInKm = region.getDouble("radius");
            }

            /**
             * Haversine formula is used to calculate distance between
             * two coordinates on sphere-like objects, like earth.
             *
             * Haversine formula is given as follows, where 2r = 2 * 6371 is Earth's
             * diameter.
             *
             * Given two points, p1 = (lat1, lon1) and p2 = (lat2, lon2),
             * the distance d between them is found as follows.
             *
             * a = sin^2((lat2 - lat1) / 2)
             * + cos(lat1) * cos(lat2) * sin^2((lon2 - lon1) / 2)
             *
             * d = 2 * r * arcsin(sqrt(a))
             *
             */

            String a = String.join(" + ",
                    String.format("POWER(SIN((RADIANS(Location.latitude) - RADIANS(%f)) / 2), 2)", centerLatitude),
                    String.join(" * ",
                            "COS(RADIANS(Location.latitude))",
                            String.format("COS(RADIANS(%f))", centerLatitude),
                            String.format("POWER(SIN((RADIANS(Location.longitude) - RADIANS(%f)) / 2), 2)",
                                    centerLongitude)));

            String d = String.format("2 * 6371 * ASIN(SQRT(%s))", a);

            String regionFilter = String.format("%f >= %s", radiusInKm, d);

            filters.add(regionFilter);
        }

        if (requestBody.has("minPricePerNight")) {
            String filter = String.format("pricePerNight >= %d", requestBody.getInt("minPricePerNight"));
            filters.add(filter);
        }

        if (requestBody.has("maxPricePerNight")) {
            String filter = String.format("pricePerNight <= %d", requestBody.getInt("maxPricePerNight"));
            filters.add(filter);
        }

        if (requestBody.has("location")) {
            JSONObject location = requestBody.getJSONObject("location");

            if (location.has("country")) {
                String filter = String.format("countryName='%s'", location.getString("country"));

                if (location.has("subcountry")) {
                    filter = String.format("%s AND subcountryName='%s'", filter, location.getString("subcountry"));

                    if (location.has("city")) {
                        filter = String.format("%s AND cityName='%s'", filter, location.getString("city"));

                        if (location.has("postalCode")) {
                            filter = String.format("%s AND postalCode='%s'", filter, location.getString("postalCode"));

                            if (location.has("streetName")) {
                                filter = String.format("%s AND streetName='%s'", filter,
                                        location.getString("streetName"));
                            }
                        }
                    }
                }

                filters.add(filter);
            }
        }

        if (requestBody.has("startDate")) {
            String filter = String.format("availabilityStart<='%s'", requestBody.getString("startDate"));
            filters.add(filter);
        }

        if (requestBody.has("endDate")) {
            String filter = String.format("availabilityEnd>='%s'", requestBody.getString("endDate"));
            filters.add(filter);
        }

        if (requestBody.has("amenities")) {
            String amentiesString = requestBody.getJSONArray("amenities").toString()
                    .replace("[", "(").replace("]", ")").replaceAll("\"", "'");
            String filter = String.format(
                    "EXISTS(SELECT * FROM includes WHERE includes.residenceId=Residence.id AND amenityName IN %s)",
                    amentiesString);
            filters.add(filter);
        }

        return String.join(" AND ", filters);
    }
}
