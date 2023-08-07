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

public class CancellationRankings extends Viewset {

    public CancellationRankings(DAO dao) {
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
                    e.put("username", rs.getString("username"));
                    e.put("numberOfCancelledBookings", rs.getInt("numberOfCancelledBookings"));
                    res.add(e);
                }

                response.put("ranking", res);

                return response;
            }
        }

        JSONObject renterRanking;
        String renterQuery = "SELECT renterUsername as username, COUNT(Booking.id) AS numberOfCancelledBookings FROM Booking WHERE cancelledBy='RENTER' GROUP BY username ORDER BY numberOfCancelledBookings DESC";

        try {
            renterRanking = this.dao.get(renterQuery, new DaoFn());
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        JSONObject hostRanking;
        String hostQuery = "SELECT hostUsername as username, COUNT(Booking.id) AS numberOfCancelledBookings FROM (Booking JOIN Listing ON Listing.id=Booking.listingId JOIN Residence ON Residence.id=Listing.residenceId) WHERE cancelledBy='HOST' GROUP BY username ORDER BY numberOfCancelledBookings DESC";

        try {
            hostRanking = this.dao.get(hostQuery, new DaoFn());
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        JSONObject res = new JSONObject();
        System.out.println("asdasd");
        res.put("hostRankings", hostRanking.getJSONArray("ranking"));
        res.put("renterRankings", renterRanking.getJSONArray("ranking"));

        return Http.JSON_RESPONSE(res, Http.STATUS.OK);
    }
}
