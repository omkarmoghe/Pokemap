package com.omkarmoghe.pokemap.network;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.omkarmoghe.pokemap.common.Notifier;
import com.omkarmoghe.pokemap.utils.Varint;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EncounterResult;
import com.pokegoapi.auth.GoogleLogin;
import com.pokegoapi.auth.PtcLogin;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;
import okhttp3.OkHttpClient;

/**
 * Created by vanshilshah on 20/07/16.
 */
public class NianticManager {
    public static final String TAG = "NianticManager";
    private static final String BASE_URL = "https://sso.pokemon.com/sso/";
    private static final String LOGIN_URL = "https://sso.pokemon.com/sso/login?service=https://sso.pokemon.com/sso/oauth2.0/callbackAuthorize";
    private static final String LOGIN_OAUTH = "https://sso.pokemon.com/sso/oauth2.0/accessToken";
    private static final String PTC_CLIENT_SECRET = "w8ScCUXJQc6kXKw8FiOhd8Fixzht18Dq3PEVkUCP5ZPxtgyWsbTvWHFLm2wNY0JR";
    public static final String CLIENT_ID = "mobile-app_pokemon-go";
    public static final String REDIRECT_URI = "https://www.nianticlabs.com/pokemongo/error";

    private static NianticManager instance;

    private List<Listener> listeners;
    private Context context;
    private AuthInfo mAuthInfo;

    final OkHttpClient client;
    private Handler mHandler;

    //private String token;

    public static NianticManager getInstance(Context context){
        if(instance == null){
            instance = new NianticManager();
            instance.context = context;

            HandlerThread thread = new HandlerThread("Niantic Manager Thread");
            thread.start();
            instance.mHandler = new Handler(thread.getLooper());
        }
        return instance;
    }

    private NianticManager() {
        listeners = new ArrayList<>();

        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void login(final String username, final String password) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try
                {
                    mAuthInfo = new PtcLogin(client).login(username, password);
                    Notifier.instance().dispatchOnLogin(mAuthInfo, new PokemonGo(mAuthInfo, client));

                } catch (LoginFailedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void fetchCatchablePokemon(final double lat, final double longitude, final double alt){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    PokemonGo pokemonGO = new PokemonGo(mAuthInfo, client);
                    pokemonGO.setLocation(lat, longitude, alt);
                    List<CatchablePokemon> catchablePokemon = pokemonGO.getMap().getCatchablePokemon();
                    Notifier.instance().dispatchOnCatchablePokemonFound(catchablePokemon);

//                  TODO: Demonstrates how to try to catch a catchable pokemon
//                  for(CatchablePokemon pokemon : catchablePokemon) {
//                        EncounterResult encounterResult = pokemon.encounterPokemon();
//                        if (encounterResult.wasSuccessful()) {
//                            CatchResult result = pokemon.catchPokemon();
//                        }
//                    }


                } catch (RemoteServerException e) {
                    e.printStackTrace();
                } catch (LoginFailedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //TODO: Find right place for this
    private ArrayList<Integer> getNeighbors() {

        Integer origin = null;
        final TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
            if (location != null) {
                origin = location.getCid();
            }
        }

        if (origin == null) {
            return null;
        }

        ArrayList<Integer> walk = new ArrayList<>();
        walk.add(origin);

        for (int i = 1; i <= 10; i++) {
            Integer next = origin + i;
            Integer prev = origin - i;
            walk.add(next);
            walk.add(prev);
        }

        Collections.sort(walk);

        return walk;
    }

    //TODO: Find right place for this
    private byte[] encode(ArrayList<Integer> walk) {
        if (walk == null) {
            return null;
        }

        byte[] mainBytes = null;

        for (Integer cellid: walk) {

            byte[] bytes = Varint.writeUnsignedVarInt(cellid);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try {
                if (mainBytes != null) {
                    outputStream.write(mainBytes);
                }
                outputStream.write( bytes );
            } catch (IOException e) {
                e.printStackTrace();
            }

            mainBytes = outputStream.toByteArray( );

        }

        assert mainBytes != null;
        return mainBytes;
    }

    public interface Listener {
        void onLogin(AuthInfo info, PokemonGo pokemonGo);
        void onOperationFailure(Exception ex);

        void onCatchablePokemonFound(List<CatchablePokemon> pokemons);
    }

}
