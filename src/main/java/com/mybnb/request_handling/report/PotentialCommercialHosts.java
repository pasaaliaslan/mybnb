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

public class PotentialCommercialHosts extends Viewset {

    public PotentialCommercialHosts(DAO dao) {
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
                    e.put("listingCount", rs.getDouble("listingCount"));
                    e.put("marketShare", rs.getDouble("marketShare"));
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

        String T = "(Listing JOIN Residence ON Listing.residenceId=Residence.Id JOIN Location ON Residence.longitude=Location.longitude AND Residence.latitude=Location.latitude)";

        if (queryParams.has("country")) {
            T = String.format("%s WHERE countryName='%s'", T,
                    queryParams.getString("country"));

            if (queryParams.has("subcountry")) {
                T = String.format("%s AND subcountryName='%s'", T,
                        queryParams.getString("subcountry"));

                if (queryParams.has("city")) {
                    T = String.format("%s AND cityName='%s'", T,
                            queryParams.getString("city"));

                    if (queryParams.has("postalCode")) {
                        T = String.format("%s AND postalCode='%s'", T,
                                queryParams.getString("postalCode"));
                    }
                }
            }
        }

        String sqlString = String.join(" ",
                "SELECT * FROM (",
                "SELECT hostUsername, COUNT(Listing.id) as listingCount, COUNT(Listing.id) / (SELECT COUNT(Listing.id) FROM",
                T,
                ") AS marketShare FROM",
                T,
                "GROUP BY hostUsername ORDER BY marketShare DESC)",
                "AS T WHERE marketShare > 0.10");

        return sqlString;
    }
}
