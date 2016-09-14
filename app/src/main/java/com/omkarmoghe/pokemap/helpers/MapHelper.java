package com.omkarmoghe.pokemap.helpers;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by localuser on 7/25/2016.
 */
public class MapHelper {
    private static final String TAG = "MapHelper";

    //Radius of the Earth in km
    public static final double EARTH = 6371;

    // Layers
    public static final float LAYER_MY_SEARCH = 0;
    public static final float LAYER_SCANNED_LOCATIONS = 50;
    public static final float LAYER_POKESTOPS = 100;
    public static final float LAYER_GYMS = 150;
    public static final float LAYER_POKEMONS = 200;

    public static final int SCAN_RADIUS = 70;
    private static final double DISTANCE_BETWEEN_CIRCLES = 121;

    /**
     * Returns the distance from 'this' point to destination point (using haversine formula).
     *
     * @return Distance between this point and destination point, in same units as radius.
     */
    public static double distance(LatLng pointA, LatLng pointB){
        //Getting the coordinates and converting them to radians
        double lat = Math.toRadians(pointA.latitude);
        double lat2 = Math.toRadians(pointB.latitude);

        //Getting the differences between the Lat and Long coordinates and
        //converting them to radians
        double difLat = Math.toRadians(pointB.latitude - pointA.latitude);
        double difLong = Math.toRadians(pointB.longitude - pointA.longitude);

        //Calculating the Haversine formula - a
        double a = (Math.pow(Math.sin(difLat/2), 2)) +
                (Math.cos(lat) * Math.cos(lat2) * Math.pow(Math.sin(difLong/2), 2));

        //Calculating the Haversine formula - c
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        //Calculating the distance using c and the radius of the earth
        return EARTH * c;
    }

    /**
     * Returns the destination point from the starting point point having travelled the given distance on the
     * given initial bearing (bearing normally varies around path followed).
     * @param point - original location in the map to translate
     * @param distance - the distance between the current point and the new point
     * @param bearing - the bearing of the object moving (clockwise from north) in degrees (0-360)
     * @return An ArcGIS Point class storing the the Latitude and Longitude in decimal degrees.
     *  The x coordinate is the Longitude, the y coordinate is the Latitude
     */
    public static LatLng translatePoint(LatLng point, double distance, double bearing){
        distance = distance/1000d;

        //converts the Latitude, Longitude and bearings into radians
        double lat = Math.toRadians(point.latitude);
        double lng = Math.toRadians(point.longitude);
        bearing = Math.toRadians(bearing);

        //Give the distance and the first Latitude, computes the second latitude
        double Lat2 = Math.asin( (Math.sin(lat)*Math.cos(distance/EARTH)) +
                (Math.cos(lat)*Math.sin(distance/EARTH)*Math.cos(bearing)) );

        //Give the distance and the first longitude, computes the second longitude
        double Long2 = lng + Math.atan2(Math.sin(bearing)*Math.sin(distance/EARTH)*Math.cos(lat),
                Math.cos(distance/EARTH)- (Math.sin(lat)*Math.sin(Lat2)) );

        //Converting the new Latitude and Longitude from radians to degrees
        Lat2 = Math.toDegrees(Lat2);
        Long2 = Math.toDegrees(Long2);

        //Creates a point object to return back. X is the longitude, Y is the Latitude
        return new LatLng(Lat2, Long2);
    }


    public static List<LatLng> getSearchArea(int steps, LatLng center){

        List<LatLng> searchArea = new ArrayList<>();
        searchArea.add(center);

        int count = 0;
        for (int i = 2; i <= steps; i++) {

            LatLng prev = searchArea.get(searchArea.size() - (1 + count));
            LatLng next = MapHelper.translatePoint(prev, MapHelper.DISTANCE_BETWEEN_CIRCLES, 0.0);
            searchArea.add(next);

            // go east
            for (int j = 0; j < i - 1; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = MapHelper.translatePoint(prev, MapHelper.DISTANCE_BETWEEN_CIRCLES, 120.0);
                searchArea.add(next);
            }

            // go due south
            for (int j = 0; j < i - 1; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = MapHelper.translatePoint(prev, MapHelper.DISTANCE_BETWEEN_CIRCLES, 180.0);
                searchArea.add(next);
            }
            // go south-west
            for (int j = 0; j < i - 1 ; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = MapHelper.translatePoint(prev, MapHelper.DISTANCE_BETWEEN_CIRCLES, 240.0);
                searchArea.add(next);
            }
            // go north-west
            for (int j = 0; j < i - 1; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = MapHelper.translatePoint(prev, MapHelper.DISTANCE_BETWEEN_CIRCLES, 300.0);
                searchArea.add(next);
            }
            // go due north
            for (int j = 0; j < i - 1; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = MapHelper.translatePoint(prev, MapHelper.DISTANCE_BETWEEN_CIRCLES, 0.0);
                searchArea.add(next);
            }
            // go north-east
            for (int j = 0; j < i - 2; j ++) {
                prev = searchArea.get(searchArea.size() - 1);
                next = MapHelper.translatePoint(prev, MapHelper.DISTANCE_BETWEEN_CIRCLES, 60.0);
                searchArea.add(next);
            }
            count = 6*(i-1)-1;
        }

        return searchArea;
    }


    public static double convertStepsToRadius(int steps) {
        return (steps - 1) * DISTANCE_BETWEEN_CIRCLES + SCAN_RADIUS;
    }
}
