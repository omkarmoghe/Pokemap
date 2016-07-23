package com.omkarmoghe.pokemap.controllers.net;

import android.os.HandlerThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omkarmoghe.pokemap.models.events.CatchablePokemonEvent;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.omkarmoghe.pokemap.models.events.LoginEventResult;
import com.omkarmoghe.pokemap.models.events.ServerUnreachableEvent;
import com.omkarmoghe.pokemap.models.events.TokenExpiredEvent;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.GoogleLogin;
import com.pokegoapi.auth.PtcLogin;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.pokegoapi.auth.PtcLogin.CLIENT_ID;
import static com.pokegoapi.auth.PtcLogin.CLIENT_SECRET;
import static com.pokegoapi.auth.PtcLogin.LOGIN_OAUTH;
import static com.pokegoapi.auth.PtcLogin.LOGIN_URL;
import static com.pokegoapi.auth.PtcLogin.REDIRECT_URI;

/**
 * Created by vanshilshah on 20/07/16.
 */
public class NianticManager {
    private static final String TAG = "NianticManager";
    private static final NianticManager instance = new NianticManager();

    private Handler mHandler;
    private AuthInfo mAuthInfo;
    private final OkHttpClient mPoGoClient;
    private PokemonGo mPokemonGo;

    public static NianticManager getInstance(){
        return instance;
    }

    private NianticManager(){
        mPoGoClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        HandlerThread thread = new HandlerThread("Niantic Manager Thread");
        thread.start();
        mHandler = new Handler(thread.getLooper());
    }

    /**
     * Sets the google auth token for the auth info also invokes the onLogin callback.
     * @param token - a valid google auth token.
     */
    public void setGoogleAuthToken(@NonNull final String token) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mAuthInfo = new GoogleLogin(mPoGoClient).login(token);
                    mPokemonGo = new PokemonGo(mAuthInfo, mPoGoClient);
                    EventBus.getDefault().post(new LoginEventResult(true, mAuthInfo, mPokemonGo));
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
                    mAuthInfo = new PtcLogin(mPoGoClient).login(username, password);
                    mPokemonGo = new PokemonGo(mAuthInfo, mPoGoClient);
                    EventBus.getDefault().post(new LoginEventResult(true, mAuthInfo, mPokemonGo));
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
                    mPokemonGo.setLocation(lat, longitude, alt);
                    EventBus.getDefault().post(new CatchablePokemonEvent(mPokemonGo.getMap().getCatchablePokemon()));
                } catch (LoginFailedException e) {
                    EventBus.getDefault().post(new TokenExpiredEvent()); //Because we aren't coming from a log in event, the token must have expired.
                } catch (RemoteServerException e) {
                    EventBus.getDefault().post(new ServerUnreachableEvent(e));
                }
            }
        });
    }

}
