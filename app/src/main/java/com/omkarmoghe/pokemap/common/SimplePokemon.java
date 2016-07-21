package com.omkarmoghe.pokemap.common;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vanshilshah on 20/07/16.
 */
public class SimplePokemon {
    private int id;
    private String name;
    private double lat;
    private double lng;
    private long expireTime;

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }
}
