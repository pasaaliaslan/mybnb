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

public class City extends Viewset {

    public City(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();
                ArrayList<String> cities = new ArrayList<String>();

                while (rs.next()) {
                    cities.add(rs.getString("name"));
                }

                response.put("cities", cities);

                return response;
            }
        }

        JSONObject responseBody;

        String country = queryParams.getString("country");
        String subcountry = queryParams.getString("subcountry");

        String sqlQuery = String.format("SELECT name FROM City WHERE countryName='%s' AND subcountryName='%s'", country,
                subcountry);

        try {
            responseBody = this.dao.get(sqlQuery, new DaoFn());
            return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }
    }

}
