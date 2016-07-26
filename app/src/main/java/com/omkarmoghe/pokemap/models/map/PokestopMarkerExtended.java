package com.omkarmoghe.pokemap.models.map;

import com.google.android.gms.maps.model.Marker;
import com.pokegoapi.api.map.fort.Pokestop;

/**
 * Created by socrates on 7/25/2016.
 */
public class PokestopMarkerExtended {


    private Pokestop pokestop;
    private Marker marker;

    public PokestopMarkerExtended(Pokestop pokestop, Marker marker) {
        this.pokestop = pokestop;
        this.marker = marker;
    }

    public Pokestop getPokestop() {
        return pokestop;
    }

    public void setPokestop(Pokestop pokestop) {
        this.pokestop = pokestop;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
