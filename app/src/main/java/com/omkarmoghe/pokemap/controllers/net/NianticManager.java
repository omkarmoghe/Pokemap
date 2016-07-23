package com.omkarmoghe.pokemap.controllers.net;

import com.omkarmoghe.pokemap.models.events.CatchablePokemonEvent;
import com.omkarmoghe.pokemap.models.events.IEvent;
import com.omkarmoghe.pokemap.models.events.LoginEventResult;
import com.omkarmoghe.pokemap.models.events.ServerUnreachableEvent;
import com.omkarmoghe.pokemap.models.events.TokenExpiredEvent;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.auth.PtcLogin;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;
import okhttp3.OkHttpClient;

/**
 * Created by vanshilshah on 20/07/16.
 */
public class NianticManager {

    private static final NianticManager instance = new NianticManager();

    private RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo mAuthInfo;
    private final OkHttpClient client;

    public static NianticManager getInstance(){
        return instance;
    }

    private NianticManager(){
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public IEvent login(final String username, final String password) {
        try {
            mAuthInfo = new PtcLogin(client).login(username, password);
            return new LoginEventResult(true, mAuthInfo, new PokemonGo(mAuthInfo, client));
        } catch (LoginFailedException e) {
            return new LoginEventResult(false, null, null);
        } catch (RemoteServerException e) {
            return new ServerUnreachableEvent(e);
        }
    }

    public IEvent getCatchablePokemon(final double lat, final double longitude, final double alt){
       try {
           PokemonGo pokemonGO = new PokemonGo(mAuthInfo, client);
           pokemonGO.setLocation(lat, longitude, alt);
           return new CatchablePokemonEvent(pokemonGO.getMap().getCatchablePokemon());
       } catch (LoginFailedException e) {
           return new TokenExpiredEvent(); //Because we aren't coming from a log in event, the token must have expired.
       } catch (RemoteServerException e) {
           return new ServerUnreachableEvent(e);
       }
    }

}
