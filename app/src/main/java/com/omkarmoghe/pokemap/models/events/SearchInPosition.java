package com.omkarmoghe.pokemap.models.events;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Link on 23/07/2016.
 */
public class SearchInPosition {

    private LatLng position;
    private int steps;

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
