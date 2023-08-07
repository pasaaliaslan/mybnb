package com.mybnb.request_handling.geography;

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

public class Country extends Viewset {

    public Country(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();
                ArrayList<String> countries = new ArrayList<String>();

                while (rs.next()) {
                    countries.add(rs.getString("name"));
                }

                response.put("countries", countries);

                return response;
            }
        }

        JSONObject responseBody;

        String sqlQuery = "SELECT name FROM Country";

        try {
            responseBody = this.dao.get(sqlQuery, new DaoFn());
            return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }
    }

}
