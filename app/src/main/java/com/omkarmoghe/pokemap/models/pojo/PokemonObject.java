package com.omkarmoghe.pokemap.models.pojo;

import POGOProtos.Enums.PokemonIdOuterClass;

/**
 * Created by furmadav on 24.7.16.
 */
public class PokemonObject {

    private final PokemonIdOuterClass.PokemonId pokemonId;
    private final double latitude;
    private final double longitude;
    private long expirationTimestamp;


    public PokemonObject(PokemonIdOuterClass.PokemonId pokemonId, double latitude, double longitude, long expirationTimestamp) {
        this.pokemonId = pokemonId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.expirationTimestamp = expirationTimestamp;
    }

    public PokemonIdOuterClass.PokemonId getPokemonId() {
        return pokemonId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public void setExpirationTimestamp(long expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PokemonObject){
            PokemonObject pokeObj = (PokemonObject) obj;
            return pokeObj.getPokemonId().equals(this.getPokemonId());
        }

        return false;
    }
}

