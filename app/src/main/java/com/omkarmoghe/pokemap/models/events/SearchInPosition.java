package com.omkarmoghe.pokemap.models.events;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Link on 23/07/2016.
 */
public class SearchInPosition {

    private LatLng position;

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }
}
