package com.omkarmoghe.pokemap.models.events;

import com.pokegoapi.api.map.fort.Pokestop;

import java.util.Collection;

/**
 * Created by socrates on 7/23/2016.
 */
public class PokestopsEvent implements IEvent {

    private Collection<Pokestop> pokestops;
    private double lat;
    private double longitude;

    public PokestopsEvent(Collection<Pokestop> pokestops, double lat, double longitude) {
        this.pokestops = pokestops;
        this.lat = lat;
        this.longitude = longitude;
    }

    public Collection<Pokestop> getPokestops() {
        return pokestops;
    }

    public void setPokestops(Collection<Pokestop> pokestops) {
        this.pokestops = pokestops;
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
