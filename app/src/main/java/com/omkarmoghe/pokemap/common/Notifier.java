package com.omkarmoghe.pokemap.common;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.omkarmoghe.pokemap.network.NianticManager;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import java.util.ArrayList;
import java.util.List;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;


/**
 * Created by coreymann on 7/22/16.
 */

public class Notifier {

    //region Singleton Methods
    private static final Notifier INSTANCE = new Notifier();

    public static Notifier instance(){
        return INSTANCE;
    }
    //endregion


    //region Members
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<NianticManager.Listener> mListeners = new ArrayList<>();


    public void addListener(NianticManager.Listener listener){ this.mListeners.add(listener); }
    public void removeListener(NianticManager.Listener listener) { this.mListeners.remove(listener); }


    public void dispatchOnLogin(final AuthInfo info, final PokemonGo pokemonGo){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for(NianticManager.Listener l : mListeners){
                    try{
                        l.onLogin(info, pokemonGo);
                    }catch(Exception ex){
                        Log.e(NianticManager.TAG, "Listener threw Exception", ex);
                    }
                }
            }
        });
    }

    public void dispatchOnCatchablePokemonFound(final List<CatchablePokemon> pokemons){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for(NianticManager.Listener l : mListeners){
                    l.onCatchablePokemonFound(pokemons);
                }
            }
        });
    }
    public void dispatchOnOperationFailure(final Exception ex){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for(NianticManager.Listener l : mListeners){
                    try{
                        l.onOperationFailure(ex);
                    }catch(Exception ex){
                        Log.e(NianticManager.TAG, "Listener threw Exception", ex);
                    }
                }
            }
        });
    }

    //endregion

}
