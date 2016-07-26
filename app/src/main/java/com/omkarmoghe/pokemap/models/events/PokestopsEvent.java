package com.omkarmoghe.pokemap.models.events;

import com.pokegoapi.api.map.fort.Pokestop;

import java.util.Collection;

/**
 * Created by socrates on 7/23/2016.
 */
public class PokestopsEvent implements IEvent {

    private Collection<Pokestop> pokestops;

    public PokestopsEvent(Collection<Pokestop> pokestops) {
        this.pokestops = pokestops;
    }

    public Collection<Pokestop> getPokestops() {
        return pokestops;
    }

    public void setPokestops(Collection<Pokestop> pokestops) {
        this.pokestops = pokestops;
    }
}
