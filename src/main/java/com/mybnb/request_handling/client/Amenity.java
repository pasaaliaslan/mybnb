package com.mybnb.request_handling.client;

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

public class Amenity extends Viewset {

    public Amenity(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();

                while (rs.next()) {
                    String amenityCategory = rs.getString("amenityCategoryName");
                    String amenity = rs.getString("name");

                    if (response.has(amenityCategory)) {
                        response.append(amenityCategory, amenity);
                    } else {
                        response.put(amenityCategory, new ArrayList<String>() {
                            {
                                add(amenity);
                            }
                        });
                    }
                }

                return response;
            }
        }

        String sqlQuery = "SELECT * FROM Amenity";

        try {
            JSONObject responseBody = this.dao.get(sqlQuery, new DaoFn());
            return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }
    }

}
