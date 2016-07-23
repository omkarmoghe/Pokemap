package com.omkarmoghe.pokemap.controllers.net;

import android.os.HandlerThread;

import com.omkarmoghe.pokemap.models.events.CatchablePokemonEvent;
import com.omkarmoghe.pokemap.models.events.IEvent;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.omkarmoghe.pokemap.models.events.LoginEventResult;
import com.omkarmoghe.pokemap.models.events.ServerUnreachableEvent;
import com.omkarmoghe.pokemap.models.events.TokenExpiredEvent;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.auth.GoogleLogin;
import com.pokegoapi.auth.PtcLogin;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import org.greenrobot.eventbus.EventBus;

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

    private Handler mHandler;
    private RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo mAuthInfo;
    private final OkHttpClient client;
    private PokemonGo pokemonGo;

    public static NianticManager getInstance(){
        return instance;
    }

    private NianticManager(){
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        HandlerThread thread = new HandlerThread("Niantic Manager Thread");
        thread.start();
        mHandler = new Handler(thread.getLooper());
    }

    /**
     * Sets the google auth token for the auth info  also invokes the onLogin callback.
     * @param token - a valid google auth token.
     */
    public void setGoogleAuthToken(@NonNull final String token) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mAuthInfo = new GoogleLogin(client).login(token);
                    pokemonGo = new PokemonGo(mAuthInfo, client);
                    EventBus.getDefault().post(new LoginEventResult(true, mAuthInfo, pokemonGo));
                } catch (LoginFailedException e) {
                    e.printStackTrace();
                } catch (RemoteServerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void login(@NonNull final String username, @NonNull final String password) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mAuthInfo = new PtcLogin(client).login(username, password);
                    pokemonGo = new PokemonGo(mAuthInfo, client);
                    EventBus.getDefault().post(new LoginEventResult(true, mAuthInfo, pokemonGo));
                } catch (LoginFailedException e) {
                    EventBus.getDefault().post(new LoginEventResult(false, null, null));
                } catch (RemoteServerException e) {
                    EventBus.getDefault().post(new ServerUnreachableEvent(e));
                }
            }
        });
    }

    public void getCatchablePokemon(final double lat, final double longitude, final double alt){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    pokemonGo.setLocation(lat, longitude, alt);
                    EventBus.getDefault().post(new CatchablePokemonEvent(pokemonGo.getMap().getCatchablePokemon()));
                } catch (LoginFailedException e) {
                    EventBus.getDefault().post(new TokenExpiredEvent()); //Because we aren't coming from a log in event, the token must have expired.
                } catch (RemoteServerException e) {
                    EventBus.getDefault().post(new ServerUnreachableEvent(e));
                }
            }
        });
    }

}
