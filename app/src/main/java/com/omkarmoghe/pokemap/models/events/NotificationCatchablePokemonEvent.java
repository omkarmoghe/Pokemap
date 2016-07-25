package com.omkarmoghe.pokemap.models.events;

import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import java.util.List;

/**
 * Created by james on 7/24/2016.
 */
public class NotificationCatchablePokemonEvent implements IEvent  {

    private List<CatchablePokemon> catchablePokemon;

    public NotificationCatchablePokemonEvent(List<CatchablePokemon> catchablePokemon) {
        this.catchablePokemon = catchablePokemon;
    }

    public List<CatchablePokemon> getCatchablePokemon() {
        return catchablePokemon;
    }

    public void setCatchablePokemon(List<CatchablePokemon> catchablePokemon) {
        this.catchablePokemon = catchablePokemon;
    }
}
