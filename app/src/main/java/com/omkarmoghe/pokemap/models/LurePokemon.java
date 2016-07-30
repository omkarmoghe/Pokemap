package com.omkarmoghe.pokemap.models;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import POGOProtos.Map.Fort.FortDataOuterClass;

/**
 * Created by chris on 7/29/2016.
 */

public class LurePokemon extends CatchablePokemon{
    private final String spawnPointId;
    public LurePokemon(PokemonGo api, FortDataOuterClass.FortData proto) {
        super(api, proto);
        spawnPointId = "Fort:" + proto.getId();
    }

    @Override
    public String getSpawnPointId() {
        return spawnPointId;
    }
}
