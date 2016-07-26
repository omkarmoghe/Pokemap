package com.omkarmoghe.pokemap.models.events;

import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import java.util.List;

/**
 * Created by Jon on 7/23/2016.
 */
public class CatchablePokemonEvent implements IEvent {

    private List<CatchablePokemon> catchablePokemon;
    private double lat;
    private double longitude;

    public CatchablePokemonEvent(List<CatchablePokemon> catchablePokemon, double lat, double longitude) {
        this.catchablePokemon = catchablePokemon;
        this.lat = lat;
        this.longitude = longitude;
    }

    public List<CatchablePokemon> getCatchablePokemon() {
        return catchablePokemon;
    }

    public void setCatchablePokemon(List<CatchablePokemon> catchablePokemon) {
        this.catchablePokemon = catchablePokemon;
    }

    public double getLat() {
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
