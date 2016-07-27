package com.omkarmoghe.pokemap.models.events;

import com.google.android.gms.maps.model.Marker;
import com.omkarmoghe.pokemap.models.map.PokemonMarkerExtended;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

/**
 * Created by chris on 7/26/2016.
 */

public class MarkerExpired {

    private PokemonMarkerExtended mData;

    public MarkerExpired(PokemonMarkerExtended markerData){
        mData = markerData;
    }

    public PokemonMarkerExtended getData(){
        return mData;
    }

    public Marker getMarker(){
        return mData.getMarker();
    }

    public CatchablePokemon getPokemon(){
        return mData.getCatchablePokemon();
    }
}
