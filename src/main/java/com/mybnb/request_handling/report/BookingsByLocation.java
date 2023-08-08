
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

public class BookingsByLocation extends Viewset {

    public BookingsByLocation(DAO dao) {
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
                "SELECT Booking.id FROM",
                "(Booking JOIN Listing ON Booking.listingId=Listing.id JOIN Residence ON Residence.id=Listing.residenceId JOIN Location ON Location.latitude=Residence.latitude AND Location.longitude=Residence.longitude)");

        ArrayList<String> filters = new ArrayList<String>();

        if (queryParams.has("startDate")) {
            String startDate = queryParams.getString("startDate");
            filters.add(String.format("'%s'<= Booking.startDate", startDate));
        }
        if (queryParams.has("endDate")) {
            String endDate = queryParams.getString("endDate");
            filters.add(String.format("'%s'>= Booking.endDate", endDate));
        }

        if (queryParams.has("country")) {
            filters.add(String.format("countryName='%s'", queryParams.getString("country")));

            if (queryParams.has("subcountry")) {
                filters.add(String.format("subcountryName='%s'", queryParams.getString("subcountry")));

                if (queryParams.has("city")) {
                    filters.add(String.format("cityName='%s'", queryParams.getString("city")));

                    if (queryParams.has("postalCode")) {
                        filters.add(String.format("postalCode='%s'", queryParams.getString("postalCode")));
                    }
                }
            }
        }

        if (!filters.isEmpty()) {
            sqlString = String.join(" WHERE ", sqlString,
                    filters.toString().replace(", ", " AND ").replace("[", "").replace("]", ""));
        }

        return sqlString;
    }
}
