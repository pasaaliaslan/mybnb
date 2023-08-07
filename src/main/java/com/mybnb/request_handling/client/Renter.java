package com.mybnb.request_handling.client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.DAO.GetHandler;
import com.mybnb.Http;
import com.mybnb.SQLStatusExceptions.BaseSQLStatusException;
import com.mybnb.SQLStatusExceptions.UnknownSQLException;
import com.mybnb.request_handling.HandlerResponse;
import com.mybnb.request_handling.Viewset;

public class Renter extends Viewset {

    public Renter(DAO dao) {
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
                    response.put("creditCardNumber", rs.getInt("creditCardNumber"));
                }

                return response;
            }
        }

        JSONObject responseBody;

        String username = queryParams.getString("username");
        String password = queryParams.getString("password");

        String sqlQuery = String
                .format("SELECT * FROM (User JOIN Renter on Renter.username=User.username) WHERE User.username='%s'",
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

        // Try creating row in Renter.
        mapping.put("username", requestBody.getString("username"));
        mapping.put("creditCardNumber", requestBody.getInt("creditCardNumber"));

        try {
            this.dao.create(mapping, "Renter");
        } catch (UnknownSQLException e) {
            return e.getHttpResponse();
        } catch (BaseSQLStatusException e) {

        }

        return Http.MESSAGE_RESPONSE("Renter is created.", Http.STATUS.OK);
    }

    @Override
    public HandlerResponse update(JSONObject queryParams, JSONObject requestBody) {
        HandlerResponse userUpdate = ClientHelpers.updateUserFromRequest(dao, requestBody);

        if (!Http.isSuccessResponse(userUpdate)) {
            return userUpdate;
        }

        // Update row in Renter.
        if (requestBody.has("creditCardNumber")) {
            HashMap<String, Object> newMapping = new HashMap<String, Object>();
            HashMap<String, Object> keyConditions = new HashMap<String, Object>();

            keyConditions.put("username", requestBody.getString("username"));
            newMapping.put("creditCardNumber", requestBody.getInt("creditCardNumber"));

            try {
                this.dao.update(newMapping, keyConditions, "Renter");
            } catch (UnknownSQLException e) {
                return e.getHttpResponse();
            } catch (BaseSQLStatusException e) {

            }
        }

        return Http.MESSAGE_RESPONSE("Renter is updated", Http.STATUS.OK);
    }

    @Override
    public HandlerResponse delete(JSONObject queryParams, JSONObject requestBody) {
        HashMap<String, Object> keyConditions = new HashMap<>();

        String tablesToBeHandled[][] = {
                { "ReviewHost", "renterUsername" },
                { "ReviewVisit", "renterUsername" },
                { "Booking", "renterUsername" },
                { "Renter", "username" },
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

        return Http.MESSAGE_RESPONSE("Renter is deleted.", Http.STATUS.OK);
    }

}
