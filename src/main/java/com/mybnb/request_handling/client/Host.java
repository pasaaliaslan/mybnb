package com.mybnb.request_handling.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.DAO.GetHandler;
import com.mybnb.Http;
import com.mybnb.SQLStatusExceptions.BaseSQLStatusException;
import com.mybnb.request_handling.HandlerResponse;
import com.mybnb.request_handling.Viewset;

public class Host extends Viewset {

    public Host(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();

                while (rs.next()) {
                    response.put("username", rs.getString("username"));
                    response.put("password", rs.getString("password"));
                    response.put("insuranceNumber", rs.getInt("insuranceNumber"));
                    response.put("firstName", rs.getString("firstName"));
                    response.put("lastName", rs.getString("lastName"));
                    response.put("dateOfBirth", rs.getDate("dateOfBirth").toString());
                    response.put("occupation", rs.getString("occupation"));
                }

                return response;
            }
        }

        JSONObject responseBody;

        String username = queryParams.getString("username");
        String password = queryParams.getString("password");

        String sqlQuery = String
                .format("SELECT * FROM (User JOIN Host on Host.username=User.username) WHERE User.username='%s'",
                        username);

        try {
            responseBody = this.dao.get(sqlQuery, new DaoFn());

            if (responseBody.isEmpty() || !password.equals(responseBody.get("password"))) {
                return Http.MESSAGE_RESPONSE("Username or password is invalid, please try again.",
                        Http.STATUS.BAD_REQUEST);
            }

        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }

    @Override
    public HandlerResponse create(JSONObject queryParams, JSONObject requestBody) {
        HashMap<String, Object> mapping = new HashMap<String, Object>();

        HandlerResponse userCreation = ClientHelpers.createUserFromRequest(dao, requestBody);

        if (!Http.isSuccessResponse(userCreation)) {
            return userCreation;
        }

        // Try creating row in Host.
        mapping.put("username", requestBody.getString("username"));

        try {
            this.dao.create(mapping, "Host");
        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.MESSAGE_RESPONSE("Host is created.", Http.STATUS.OK);
    }

    @Override
    public HandlerResponse update(JSONObject queryParams, JSONObject requestBody) {
        HandlerResponse userUpdate = ClientHelpers.updateUserFromRequest(dao, requestBody);

        if (!Http.isSuccessResponse(userUpdate)) {
            return userUpdate;
        }

        return Http.MESSAGE_RESPONSE("Host is updated", Http.STATUS.OK);
    }

    @Override
    public HandlerResponse delete(JSONObject queryParams, JSONObject requestBody) {
        HashMap<String, Object> keyConditions = new HashMap<>();

        String tablesToBeHandled[][] = {
                { "Residence", "hostUsername" },
                { "ReviewRenter", "hostUsername" },
                { "livesIn", "username" },
                { "User", "username" }
        };

        for (String[] table : tablesToBeHandled) {
            keyConditions.put(table[1], queryParams.getString("username"));

            try {
                this.dao.delete(keyConditions, table[0]);
            } catch (BaseSQLStatusException e) {
                return e.getHttpResponse();
            }

            keyConditions.clear();
        }

        return Http.MESSAGE_RESPONSE("Host is deleted.", Http.STATUS.OK);
    }

}
