package com.mybnb.request_handling.toolkit;

import java.util.HashMap;
import java.util.Map;

import com.mybnb.DAO;
import com.mybnb.request_handling.RequestHandler;
import com.mybnb.request_handling.Viewset;

public class ToolkitRequestHandler extends RequestHandler {

    public ToolkitRequestHandler(DAO dao) {
        super(dao);
    }

    public Map<String, Viewset> getViewsets() {
        HashMap<String, Viewset> viewsets = new HashMap<String, Viewset>();
        viewsets.put("price_suggestion", new PriceSuggestion(dao));
        return viewsets;
    }

}
