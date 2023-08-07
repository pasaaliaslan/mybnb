package com.mybnb.request_handling.client;

import java.util.HashMap;
import java.util.Map;

import com.mybnb.DAO;
import com.mybnb.request_handling.RequestHandler;
import com.mybnb.request_handling.Viewset;

public class ClientRequestHandler extends RequestHandler {

    public ClientRequestHandler(DAO dao) {
        super(dao);
    }

    public Map<String, Viewset> getViewsets() {
        HashMap<String, Viewset> viewsets = new HashMap<String, Viewset>();
        viewsets.put("renter", new Renter(this.dao));
        viewsets.put("host", new Host(this.dao));
        viewsets.put("listing", new Listing(this.dao));
        viewsets.put("booking", new Booking(this.dao));

        return viewsets;
    }

}
