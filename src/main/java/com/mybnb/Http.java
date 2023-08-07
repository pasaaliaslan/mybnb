package com.mybnb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.mybnb.request_handling.HandlerResponse;
import com.sun.net.httpserver.HttpExchange;

public class Http {

    public enum STATUS {
        OK(200),
        BAD_REQUEST(400),
        NOT_FOUND(404),
        SERVER_ERROR(500),
        NOT_IMPLEMENTED(501);

        private final int statusCode;

        STATUS(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return this.statusCode;
        }
    }

    public final static String GET = "GET";
    public final static String POST = "POST";
    public final static String PUT = "PUT";
    public final static String DELETE = "DELETE";

    public final static HandlerResponse SERVER_ERROR_RESPONSE = new HandlerResponse(
            new JSONObject().put("message", "SERVER ERROR"), STATUS.SERVER_ERROR.getStatusCode());

    public final static HandlerResponse UNSUPPORTED_METHOD_RESPONSE = new HandlerResponse(
            new JSONObject().put("message", "UNSUPPORTED METHOD"), STATUS.NOT_IMPLEMENTED.getStatusCode());

    public final static String getRequestMethod(HttpExchange exchange) {
        return exchange.getRequestMethod().toUpperCase();
    }

    public final static JSONObject getRequestBody(HttpExchange exchange) throws IOException {
        InputStream rawRequestBody = exchange.getRequestBody();
        String rawRequestBodyAsString;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(rawRequestBody))) {
            rawRequestBodyAsString = br.lines().collect(Collectors.joining(System.lineSeparator()));
        }

        if (rawRequestBodyAsString.isEmpty()) {
            return new JSONObject();
        }

        return new JSONObject(rawRequestBodyAsString);
    }

    public final static JSONObject getQueryParams(HttpExchange exchange) {
        String rawQueryParams = exchange.getRequestURI().getQuery();
        JSONObject queryParams = new JSONObject();

        if (rawQueryParams == null) {
            return queryParams;
        }

        for (String param : rawQueryParams.split("&")) {
            String[] keyValue = param.split("=");
            queryParams.put(keyValue[0], keyValue[1]);
        }

        return queryParams;
    }

    public final static String getEndpoint(HttpExchange exchange) {
        String[] pathComponents = exchange.getRequestURI().getPath().split("/");
        return pathComponents[pathComponents.length - 1];
    }

    public final static HandlerResponse MESSAGE_RESPONSE(String message, STATUS status) {
        if (status == STATUS.SERVER_ERROR) {
            message = "Server Error.";
        }

        JSONObject responseBody = new JSONObject().put("message", message);
        return new HandlerResponse(responseBody, status.getStatusCode());
    }

    public final static HandlerResponse JSON_RESPONSE(JSONObject json, STATUS status) {
        return new HandlerResponse(json, status.getStatusCode());
    }

    public final static boolean isSuccessResponse(HandlerResponse response) {
        return response.getResponseStatus() == 200;
    }
}
