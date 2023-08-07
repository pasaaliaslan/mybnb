package com.mybnb.request_handling.report;

import java.util.HashMap;
import java.util.Map;

import com.mybnb.DAO;
import com.mybnb.request_handling.RequestHandler;
import com.mybnb.request_handling.Viewset;

public class ReportRequestHandler extends RequestHandler {

    public ReportRequestHandler(DAO dao) {
        super(dao);
    }

    public Map<String, Viewset> getViewsets() {
        HashMap<String, Viewset> viewsets = new HashMap<String, Viewset>();
        viewsets.put("listings_by_location", new ListingsByLocation(dao));
        viewsets.put("bookings_by_location", new BookingsByLocation(dao));
        viewsets.put("host_rankings_by_listing", new HostRankingsByListing(dao));
        viewsets.put("potential_commercial_hosts", new PotentialCommercialHosts(dao));
        viewsets.put("renter_rankings_by_bookings", new RenterRankingsByBookings(dao));
        viewsets.put("cancellation_rankings", new CancellationRankings(dao));
        viewsets.put("most_popular_noun_phrases", new MostPopularNounPhrases(dao));
        return viewsets;
    }

}
