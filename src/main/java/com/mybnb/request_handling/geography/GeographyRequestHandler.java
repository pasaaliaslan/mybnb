package com.mybnb.request_handling.geography;

import java.util.HashMap;
import java.util.Map;

import com.mybnb.DAO;
import com.mybnb.request_handling.RequestHandler;
import com.mybnb.request_handling.Viewset;

public class GeographyRequestHandler extends RequestHandler {

    public GeographyRequestHandler(DAO dao) {
        super(dao);
    }

    @Override
    public Map<String, Viewset> getViewsets() {
        HashMap<String, Viewset> viewsets = new HashMap<String, Viewset>();
        viewsets.put("country", new Country(this.dao));
        viewsets.put("subcountry", new Subcountry(this.dao));
        viewsets.put("city", new City(this.dao));

        return viewsets;
    }

}
