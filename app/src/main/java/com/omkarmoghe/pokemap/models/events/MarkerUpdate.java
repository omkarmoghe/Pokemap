package com.omkarmoghe.pokemap.models.events;

/**
 * Created by Rohan on 26-07-2016.
 */

import com.google.android.gms.maps.model.Marker;
import com.omkarmoghe.pokemap.models.map.PokemonMarkerExtended;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

/**
 * Empty event.
 */
public class MarkerUpdate {
    private PokemonMarkerExtended mData;

    public MarkerUpdate(PokemonMarkerExtended markerData){
        mData = markerData;
    }

    public Marker getMarker(){
        return mData.getMarker();
    }

    public CatchablePokemon getPokemon(){
        return mData.getCatchablePokemon();
    }
}
