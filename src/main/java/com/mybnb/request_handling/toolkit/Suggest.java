package com.mybnb.request_handling.toolkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.DAO.GetHandler;
import com.mybnb.Http;
import com.mybnb.SQLStatusExceptions.BaseSQLStatusException;
import com.mybnb.request_handling.HandlerResponse;
import com.mybnb.request_handling.Viewset;

public class Suggest extends Viewset {

    public Suggest(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        JSONObject responseBody = new JSONObject();

        try {
            responseBody.put("suggestedPrice", this.getSuggestedPrice(requestBody));
            responseBody.put("suggestedAmenities", this.getSuggestedAmenities(requestBody));
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }

    private double getSuggestedPrice(JSONObject requestBody) throws BaseSQLStatusException {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();

                while (rs.next()) {
                    response.put("suggestedPrice", rs.getDouble("suggestedPrice"));
                }

                return response;
            }
        }

        String country = requestBody.getString("country");
        String subcountry = requestBody.getString("subcountry");
        String city = requestBody.getString("city");
        String postalCode = requestBody.getString("postalCode");

        String sqlQuery = String.join(" ",
                "SELECT AVG(pricePerNight) as suggestedPrice FROM Listing",
                "JOIN Residence ON Listing.residenceId=Residence.id",
                "JOIN Location ON Residence.latitude=Location.latitude AND Residence.longitude=Location.longitude",
                String.format("WHERE countryName='%s' AND subcountryName='%s' AND cityName='%s' AND postalCode='%s'",
                        country, subcountry, city, postalCode)

        );

        JSONObject res = this.dao.get(sqlQuery, new DaoFn());

        return res.getDouble("suggestedPrice");
    }

    private JSONArray getSuggestedAmenities(JSONObject requestBody) throws BaseSQLStatusException {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();

                List<JSONObject> amenities = new ArrayList<>();

                while (rs.next()) {
                    JSONObject e = new JSONObject();
                    e.put("name", rs.getString("amenityName"));
                    e.put("frequency", rs.getInt("frequency"));
                    amenities.add(e);
                }

                response.put("suggestedAmenities", amenities);
                return response;
            }
        }

        String country = requestBody.getString("country");
        String subcountry = requestBody.getString("subcountry");
        String city = requestBody.getString("city");
        String postalCode = requestBody.getString("postalCode");

        JSONArray providedAmenities;
        String providedAmenitiesAsString = null;

        if (requestBody.has("providedAmenities")) {
            providedAmenities = requestBody.getJSONArray("providedAmenities");

            providedAmenitiesAsString = String.format("'%s'", providedAmenities.getString(0));

            for (int i = 1; i < providedAmenities.length(); i++) {
                String amenity = providedAmenities.getString(i);

                providedAmenitiesAsString = String.format("%s, '%s'", providedAmenitiesAsString, amenity);
            }
        }

        String sqlQuery = String.join(" ",
                "SELECT amenityName, COUNT(amenityName) as frequency FROM Listing",
                "JOIN includes ON Listing.residenceId=includes.residenceId",
                "JOIN Residence ON Residence.id=Listing.residenceId",
                "JOIN Location ON Location.longitude=Residence.longitude AND Location.latitude=Residence.latitude",
                String.format("WHERE countryName='%s' AND subcountryName='%s' AND cityName='%s' AND postalCode='%s'",
                        country,
                        subcountry, city, postalCode));

        if (providedAmenitiesAsString != null) {
            sqlQuery = String.join(" ",
                    sqlQuery,
                    String.format("AND amenityName NOT IN (%s)", providedAmenitiesAsString));
        }

        sqlQuery = String.join(" ", sqlQuery, "GROUP BY amenityName ORDER BY frequency DESC");

        JSONObject res = this.dao.get(sqlQuery, new DaoFn());

        return res.getJSONArray("suggestedAmenities");
    }
}
