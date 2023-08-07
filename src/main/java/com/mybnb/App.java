package com.mybnb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import com.mybnb.request_handling.client.ClientRequestHandler;
import com.mybnb.request_handling.geography.GeographyRequestHandler;
import com.mybnb.request_handling.report.ReportRequestHandler;
import com.mybnb.request_handling.toolkit.ToolkitRequestHandler;
import com.sun.net.httpserver.HttpServer;

import io.github.cdimascio.dotenv.Dotenv;

public class App {

    private static int PORT = Integer.parseInt(Dotenv.load().get("PORT"));
    private static final String GEOGRAPHY_CONTEXT = "/api/v1/geography";
    private static final String CLIENT_CONTEXT = "/api/v1/client";
    private static final String REPORT_CONTEXT = "/api/v1/report";
    private static final String TOOLKIT_CONTEXT = "/api/v1/toolkit";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        try {
            DAO dao = DAO.getInstance();
            System.out.println("Starting server...");
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 8000);
            server.createContext(GEOGRAPHY_CONTEXT).setHandler(new GeographyRequestHandler(dao));
            server.createContext(CLIENT_CONTEXT).setHandler(new ClientRequestHandler(dao));
            server.createContext(REPORT_CONTEXT).setHandler(new ReportRequestHandler(dao));
            server.createContext(TOOLKIT_CONTEXT).setHandler(new ToolkitRequestHandler(dao));
            server.start();
            System.out.println("Server started at port " + PORT);
        } catch (SQLException e) {
            System.err.println("DB Connection error occured!");
        }
    }
}
