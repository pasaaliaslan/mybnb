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

public class HostRankingsByListing extends Viewset {

    public HostRankingsByListing(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();
                ArrayList<JSONObject> res = new ArrayList<JSONObject>();

                while (rs.next()) {
                    JSONObject e = new JSONObject();
                    e.put("hostUsername", rs.getString("hostUsername"));
                    e.put("listingCount", rs.getInt("listingCount"));
                    res.add(e);
                }

                response.put("ranking", res);

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
                "SELECT hostUsername, COUNT(Listing.id) as listingCount",
                "FROM (Listing JOIN Residence ON Listing.residenceId=Residence.Id JOIN Location ON Residence.longitude=Location.longitude AND Residence.latitude=Location.latitude)");

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

        sqlString = String.format("%s GROUP BY Residence.hostUsername ORDER BY listingCount DESC", sqlString);

        return sqlString;
    }
}
