package com.omkarmoghe.pokemap.models.events;

import java.util.Collection;

import POGOProtos.Map.Fort.FortDataOuterClass;

/**
 * Created by aronhomberg on 7/23/2016.
 */
public class GymsEvent implements IEvent {

    private Collection<FortDataOuterClass.FortData> gyms;
    private double lat;
    private double longitude;

    public GymsEvent(Collection<FortDataOuterClass.FortData> gyms, double lat, double longitude) {
        this.gyms = gyms;
        this.lat = lat;
        this.longitude = longitude;
    }

    public Collection<FortDataOuterClass.FortData> getGyms() {
        return gyms;
    }

    public void setGyms(Collection<FortDataOuterClass.FortData> gyms) {
        this.gyms = gyms;
    }

    public double getLatitude() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
