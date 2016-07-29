package com.omkarmoghe.pokemap.models.map.directions;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by carsten on 29-07-16.
 */

public class RouteOption {

    private LatLng from;
    private LatLng to;
    private String transportMode;
    private String distance;
    private String duration;
    private PolylineOptions polyLine;

    public RouteOption(LatLng from, LatLng to, String transportMode, String distance, String duration, PolylineOptions polyLine) {
        this.from = from;
        this.to = to;
        this.transportMode = transportMode;
        this.distance = distance;
        this.duration = duration;
        this.polyLine = polyLine;
    }

    public LatLng getFrom() {
        return from;
    }

    public LatLng getTo() {
        return to;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public PolylineOptions getPolyLine() {
        return polyLine;
    }
}
