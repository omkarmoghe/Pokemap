package com.omkarmoghe.pokemap.models.map.directions;

import android.text.TextUtils;

/**
 * Created by carsten on 29-07-16.
 */

public class RouteOptions {

    private RouteOption walkOption;
    private RouteOption bikeOption;
    private RouteOption carOption;

    public RouteOptions() { }

    public boolean isComplete() {
        return walkOption != null && bikeOption != null && carOption != null;
    }

    public void setWalkOption(RouteOption walkOption) {
        this.walkOption = walkOption;
    }
    public RouteOption getWalkOption() {
        return walkOption;
    }

    public void setBikeOption(RouteOption bikeOption) {
        this.bikeOption = bikeOption;
    }
    public RouteOption getBikeOption() {
        return bikeOption;
    }

    public void setCarOption(RouteOption carOption) {
        this.carOption = carOption;
    }
    public RouteOption getCarOption() {
        return carOption;
    }
}
