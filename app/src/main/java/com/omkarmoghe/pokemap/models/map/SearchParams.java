package com.omkarmoghe.pokemap.models.map;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 7/24/2016.
 */

public class SearchParams {
    private static final String TAG = "SearchParams";

    public static final int DEFAULT_RADIUS = 100;
    public static final double DISTANCE = 173.1;

    private int mRadius;
    private LatLng mCenter;

    public SearchParams(int radius, LatLng center){
        mRadius = radius;
        mCenter = center;
    }

    public List<LatLng> getSearchArea(){
        List<LatLng> searchArea = new ArrayList<>();
        searchArea.add(mCenter);
        int steps = (int) Math.ceil(mRadius/DEFAULT_RADIUS);
        Log.d(TAG, "getSearchArea: Steps = " + steps);
        int count = 0;
        for (int i = 2; i <= steps; i++) {

            LatLng prev = searchArea.get(searchArea.size() - (1 + count));
            LatLng next = prev.translatePoint(DISTANCE, 0.0);
            searchArea.add(next);

            for (int j = 0; j < i - 1; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = prev.translatePoint(DISTANCE, 120.0);
                searchArea.add(next);
            }

            // go due south
            for (int j = 0; j < i - 1; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = prev.translatePoint(DISTANCE, 180.0);
                searchArea.add(next);
            }
            // go south-west
            for (int j = 0; j < i - 1 ; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = prev.translatePoint(DISTANCE, 240.0);
                searchArea.add(next);
            }
            // go north-west
            for (int j = 0; j < i - 1; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = prev.translatePoint(DISTANCE, 300.0);
                searchArea.add(next);
            }
            // go due north
            for (int j = 0; j < i - 1; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = prev.translatePoint(DISTANCE, 0.0);
                searchArea.add(next);
            }
            // go north-east
            for (int j = 0; j < i - 2; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = prev.translatePoint(DISTANCE, 60.0);
                searchArea.add(next);
            }

            count = count == 0 ? 6 - 1 : (count+ 1) *2 - 1;
        }

        Log.d(TAG, "getSearchArea: searchArea size = " + searchArea.size());
        Log.d(TAG, "getSearchArea() returned: " + searchArea);

        return searchArea;
    }
}

