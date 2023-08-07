package com.mybnb.request_handling;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.Http;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class RequestHandler implements HttpHandler {

    public JSONObject requestBody;
    public JSONObject queryParams;
    public String endpoint;
    public String requestMethod;

    public DAO dao;

    /**
     * { endpoint: Viewset }
     */
    private Map<String, Viewset> viewsets;

    public RequestHandler(DAO dao) {
        this.dao = dao;
        this.viewsets = getViewsets();
    }

    public abstract Map<String, Viewset> getViewsets();

    public void handle(HttpExchange exchange) throws IOException {
        this.requestBody = Http.getRequestBody(exchange);
        this.queryParams = Http.getQueryParams(exchange);
        this.endpoint = Http.getEndpoint(exchange);
        this.requestMethod = Http.getRequestMethod(exchange);

        HandlerResponse response = this.getResponse();

        int responseStatus = response.getResponseStatus();
        String responseBody = response.getResponseBody();

        try {
            exchange.getResponseHeaders().add("Content-Type", "Application/json");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            exchange.sendResponseHeaders(responseStatus, responseBody.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBody.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HandlerResponse getResponse() {
        Viewset viewset = this.viewsets.get(this.endpoint);

        if (viewset == null) {
            return Http.MESSAGE_RESPONSE("Unsupported endpoint.", Http.STATUS.BAD_REQUEST);
        }

        System.out.println("=======================");
        System.out.println(String.format("%s - %s", this.requestMethod, this.endpoint));
        System.out.println("=======================");

        switch (this.requestMethod) {
            case Http.GET:
                return viewset.get(this.queryParams, this.requestBody);
            case Http.POST:
                return viewset.create(this.queryParams, this.requestBody);
            case Http.PUT:
                return viewset.update(this.queryParams, this.requestBody);
            case Http.DELETE:
                return viewset.delete(this.queryParams, this.requestBody);
        }

        return null;
    }

}
