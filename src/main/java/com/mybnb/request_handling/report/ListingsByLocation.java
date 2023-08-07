package com.mybnb.request_handling.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.DAO.GetHandler;
import com.mybnb.SQLStatusExceptions.BaseSQLStatusException;
import com.mybnb.Http;
import com.mybnb.request_handling.HandlerResponse;
import com.mybnb.request_handling.Viewset;

public class ListingsByLocation extends Viewset {

    public ListingsByLocation(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();
                ArrayList<String> listingIds = new ArrayList<String>();

                while (rs.next()) {
                    listingIds.add(rs.getString("id"));
                }

                response.put("ids", listingIds);
                response.put("count", listingIds.size());

                return response;
            }
        }

        JSONObject responseBody;

        String sqlQuery = this.buildQuery(queryParams);

        try {
            responseBody = this.dao.get(sqlQuery, new DaoFn());

        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }

    private String buildQuery(JSONObject queryParams) {
        String sqlString = String.join(" ",
                "SELECT Listing.id as id FROM Listing",
                "JOIN Residence ON Listing.residenceId=Residence.id",
                "JOIN Location ON Residence.latitude=Location.latitude AND Residence.longitude=Location.longitude");

        if (queryParams.has("country")) {
            sqlString = String.format("%s WHERE countryName='%s'", sqlString, queryParams.getString("country"));

            if (queryParams.has("subcountry")) {
                sqlString = String.format("%s AND subcountryName='%s'", sqlString, queryParams.getString("subcountry"));

                if (queryParams.has("city")) {
                    sqlString = String.format("%s AND cityName='%s'", sqlString, queryParams.getString("city"));

                    if (queryParams.has("postalCode")) {
                        sqlString = String.format("%s AND postalCode='%s'", sqlString,
                                queryParams.getString("postalCode"));
                    }
                }
            }
        }

        return sqlString;
    }
}
