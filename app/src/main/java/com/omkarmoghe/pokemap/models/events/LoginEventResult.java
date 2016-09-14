package com.omkarmoghe.pokemap.models.events;

import com.pokegoapi.api.PokemonGo;
import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;

/**
 * Created by Jon on 7/23/2016.
 */
public class LoginEventResult implements IEvent {

    private boolean loggedIn;
    private AuthInfo authInfo;
    private PokemonGo pokemonGo;

    public LoginEventResult(boolean loggedIn, AuthInfo authInfo, PokemonGo pokemonGo) {
        this.loggedIn = loggedIn;
        this.authInfo = authInfo;
        this.pokemonGo = pokemonGo;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(AuthInfo authInfo) {
        this.authInfo = authInfo;
    }

    public PokemonGo getPokemonGo() {
        return pokemonGo;
    }

    public void setPokemonGo(PokemonGo pokemonGo) {
        this.pokemonGo = pokemonGo;
    }
}
