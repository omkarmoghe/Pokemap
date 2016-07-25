package com.omkarmoghe.pokemap.models.events;

import com.pokegoapi.api.map.fort.Pokestop;

import java.util.List;

/**
 * Created by Jon on 7/23/2016.
 */
public class PokestopsEvent implements IEvent {

    private List<Pokestop> pokestopList;

    public PokestopsEvent(List<Pokestop> pokestopList) {
        this.pokestopList = pokestopList;
    }

    public List<Pokestop> getPokestopList() {
        return pokestopList;
    }

    public void setPokestopList(List<Pokestop> pokestopList) {
        this.pokestopList = pokestopList;
    }
}
