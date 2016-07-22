package com.omkarmoghe.pokemap.common;

import com.omkarmoghe.pokemap.network.NianticManager;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import java.util.List;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;

/**
 * Created by coreymann on 7/22/16.
 */

public class AbstractNianticEventListener implements NianticManager.NianticEventListener {
    @Override
    public void onLogin(RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo info, PokemonGo pokemonGo) {

    }

    @Override
    public void onOperationFailure(Exception ex) {

    }

    @Override
    public void onCatchablePokemonFound(List<CatchablePokemon> pokemons) {
        
    }
}
