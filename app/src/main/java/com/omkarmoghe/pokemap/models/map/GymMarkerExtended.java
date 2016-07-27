package com.omkarmoghe.pokemap.models.map;

import com.google.android.gms.maps.model.Marker;
import com.pokegoapi.api.map.fort.Pokestop;

import POGOProtos.Map.Fort.FortDataOuterClass;

/**
 * Created by aronhomberg on 7/26/2016.
 */
public class GymMarkerExtended {


    private FortDataOuterClass.FortData gym;
    private Marker marker;

    public GymMarkerExtended(FortDataOuterClass.FortData gym, Marker marker) {
        this.gym = gym;
        this.marker = marker;
    }

    public FortDataOuterClass.FortData getGym() {
        return gym;
    }

    public void setGym(FortDataOuterClass.FortData gym) {
        this.gym = gym;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
