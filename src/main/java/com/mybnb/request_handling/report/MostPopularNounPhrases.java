package com.mybnb.request_handling.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.mybnb.DAO;
import com.mybnb.DAO.GetHandler;
import com.mybnb.SQLStatusExceptions.BaseSQLStatusException;
import com.mybnb.Http;
import com.mybnb.request_handling.HandlerResponse;
import com.mybnb.request_handling.Viewset;

public class MostPopularNounPhrases extends Viewset {

    public MostPopularNounPhrases(DAO dao) {
        super(dao);
    }

    @Override
    public HandlerResponse get(JSONObject queryParams, JSONObject requestBody) {
        class DaoFn implements GetHandler {
            @Override
            public JSONObject execute(ResultSet rs) throws SQLException {
                JSONObject response = new JSONObject();
                ArrayList<String> comments = new ArrayList<String>();

                while (rs.next()) {
                    comments.add(rs.getString("comment"));
                }

                Map<String, Integer> freqMap = this.countNounPhrases(comments);
                response.put("freqMap", freqMap);

                return response;
            }

            private Map<String, Integer> countNounPhrases(List<String> comments) {
                Map<String, Integer> freqMap = new HashMap<String, Integer>();
                List<String> invalidWords = Arrays.asList("and", "the", "of", "a", "an", "for");

                for (String comment : comments) {
                    List<String> words = Arrays.asList(comment.toLowerCase().split("\\s+"));

                    for (int i = 0; i < words.size(); i++) {
                        String phrase = "";

                        for (int j = i; j < words.size(); j++) {
                            String addition = words.get(j);

                            if (!addition.matches("[a-zA-Z\\s]+")) {
                                break;
                            }

                            if (phrase.equals("")) {
                                phrase = addition;
                            } else {
                                phrase = String.format("%s %s", phrase, addition);
                            }

                            if (invalidWords.contains(addition)) {
                                continue;
                            }

                            freqMap.putIfAbsent(phrase, 0);
                            freqMap.put(phrase, freqMap.get(phrase) + 1);
                        }
                    }
                }

                Map<String, Integer> res = new HashMap<String, Integer>();

                for (Map.Entry<String, Integer> e : freqMap.entrySet()) {
                    if (e.getValue() >= 3 || (e.getKey().length() >= 3 && e.getValue() >= 2)) {
                        res.put(e.getKey(), e.getValue());
                    }
                }

                return res;
            }
        }

        JSONObject responseBody;
        int listingId = queryParams.getInt("listingId");

        String sqlQuery = String.format(
                "SELECT comment FROM ReviewVisit JOIN Booking ON ReviewVisit.bookingId=Booking.id WHERE listingId=%d",
                listingId);

        try {
            responseBody = this.dao.get(sqlQuery, new DaoFn());

        } catch (BaseSQLStatusException e) {
            return e.getHttpResponse();
        }

        return Http.JSON_RESPONSE(responseBody, Http.STATUS.OK);
    }
}
