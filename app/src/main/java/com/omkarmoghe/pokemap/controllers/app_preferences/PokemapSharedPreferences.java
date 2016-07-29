package com.omkarmoghe.pokemap.controllers.app_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;

import com.omkarmoghe.pokemap.models.login.GoogleLoginInfo;
import com.omkarmoghe.pokemap.models.login.LoginInfo;
import com.omkarmoghe.pokemap.models.login.PtcLoginInfo;
import com.pokegoapi.api.pokemon.Pokemon;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import java.util.HashSet;
import java.util.Set;

import POGOProtos.Enums.PokemonIdOuterClass;

/**
 * Provide convenience methods to access shared preferences
 */

public final class PokemapSharedPreferences implements PokemapAppPreferences {
    private static final String TAG = "PokemapSharedPreference";

    private static final String PTC_INFO_KEY = "PTCKey";
    private static final String GOOGLE_INFO_KEY = "GoogleKey";
    private static final String SHOW_SCANNED_PLACES = "scanned_checkbox";
    private static final String SHOW_POKESTOPS = "pokestops_checkbox";
    private static final String SHOW_GYMS = "gyms_checkbox";
    private static final String SHOW_LURED = "lured_checkbox";
    private static final String SERVICE_KEY = "background_poke_service";
    private static final String SERVICE_REFRESH_KEY = "service_refresh_rate";
    private static final String POKEMONS_TO_SHOW = "pokemons_to_show";
    private static final String STEPS = "search_steps";
    private static final String SHOW_MAP_SUGGESTION = "show_map_suggestion";

    private static final String INFO_TOKEN = "token=";
    private static final String INFO_REFRESH = "refresh=";
    private static final String INFO_USERNAME = "username=";
    private static final String INFO_PASSWORD = "password=";

    private final SharedPreferences sharedPreferences;

    public PokemapSharedPreferences(@NonNull Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public LoginInfo getLoginInfo() {

        if(sharedPreferences.contains(PTC_INFO_KEY)){
            Set<String> ptcInfo = sharedPreferences.getStringSet(PTC_INFO_KEY, null);
            if(ptcInfo != null && !ptcInfo.isEmpty()) {
                String[] info = ptcInfo.toArray(new String[3]);

                String token = null;
                String username = null;
                String password = null;

                for (String s :info) {
                    if(!TextUtils.isEmpty(s)) {
                        if (s.contains(INFO_TOKEN)) {
                            token = getStoredString(s);
                            continue;
                        }
                        if (s.contains(INFO_USERNAME)) {
                            username = getStoredString(s);
                            continue;
                        }
                        if (s.contains(INFO_PASSWORD)) {
                            password = getStoredString(s);
                        }
                    }
                }
                return new PtcLoginInfo(token, username, password);
            }
        }

        if(sharedPreferences.contains(GOOGLE_INFO_KEY)) {
            Set<String> googleInfo = sharedPreferences.getStringSet(GOOGLE_INFO_KEY, null);
            if (googleInfo != null) {
                String[] info = googleInfo.toArray(new String[2]);

                String token = null;
                String refresh = null;

                for (String s :info) {
                    if(!TextUtils.isEmpty(s)) {
                        if (s.contains(INFO_TOKEN)) {
                            token = getStoredString(s);
                            continue;
                        }
                        if (s.contains(INFO_PASSWORD)) {
                            refresh = getStoredString(s);
                        }
                    }
                }

                return new GoogleLoginInfo(token, refresh);
            }
        }

        return null;
    }

    @Override
    public void setLoginInfo(LoginInfo loginInfo) {
        Log.d(TAG, "setLoginInfo: LoginInfo = " + loginInfo);

        clearLoginCredentials();

        if(loginInfo instanceof PtcLoginInfo){
            Set<String> info = new HashSet<>();
            PtcLoginInfo ptc = (PtcLoginInfo) loginInfo;
            info.add(INFO_TOKEN + ptc.getToken());
            info.add(INFO_USERNAME + ptc.getUsername());
            info.add(INFO_PASSWORD + ptc.getPassword());
            sharedPreferences.edit().putStringSet(PTC_INFO_KEY, info).apply();
        }

        if(loginInfo instanceof GoogleLoginInfo){
            Set<String> info = new HashSet<>();
            GoogleLoginInfo google = (GoogleLoginInfo) loginInfo;
            info.add(INFO_TOKEN + google.getToken());
            info.add(INFO_REFRESH + google.getRefreshToken());
            Log.d(TAG, "setLoginInfo: Googleinfo = " + info);
            sharedPreferences.edit().putStringSet(GOOGLE_INFO_KEY, info).apply();
        }
    }

    private String getStoredString(String value){
        if (value == null) return null;
        String[] parts = value.split("=");
        
        return (parts.length > 1) ? parts[1] : null;
    }

    @Override
    public boolean isLoggedIn() {
        return sharedPreferences.contains(PTC_INFO_KEY) || sharedPreferences.contains(GOOGLE_INFO_KEY);
    }

    @Override
    public void clearLoginCredentials() {

        sharedPreferences.edit().remove(GOOGLE_INFO_KEY).apply();
        sharedPreferences.edit().remove(PTC_INFO_KEY).apply();
    }

    @Override
    public boolean getShowScannedPlaces() {
        return sharedPreferences.getBoolean(SHOW_SCANNED_PLACES, false);
    }

    @Override
    public boolean getShowPokestops() {
        return sharedPreferences.getBoolean(SHOW_POKESTOPS, false);
    }

    @Override
    public int getSteps() {
        return Integer.parseInt(sharedPreferences.getString(STEPS, "3"));
    }

    @Override
    public boolean getShowGyms() {
        return sharedPreferences.getBoolean(SHOW_GYMS, false);
    }

    @Override
    public boolean getShowLuredPokemon() {
        return sharedPreferences.getBoolean(SHOW_LURED, true);
    }

    public Set<PokemonIdOuterClass.PokemonId> getShowablePokemonIDs() {
        Set<String> showablePokemonStringIDs = sharedPreferences.getStringSet(POKEMONS_TO_SHOW, null);
        if(showablePokemonStringIDs == null) {
            //Provides the filter with all available pokemon if no filter is set.
            showablePokemonStringIDs = new HashSet<>();
            for (PokemonIdOuterClass.PokemonId pokemonId : PokemonIdOuterClass.PokemonId.values()) {
                if(pokemonId != PokemonIdOuterClass.PokemonId.UNRECOGNIZED) {
                    showablePokemonStringIDs.add(String.valueOf(pokemonId.getNumber()));
                }
            }
        }
        Set<PokemonIdOuterClass.PokemonId> showablePokemonIDs = new HashSet<>();
        for (String stringId : showablePokemonStringIDs) {
            showablePokemonIDs.add(PokemonIdOuterClass.PokemonId.forNumber(Integer.valueOf(stringId)));
        }
        return showablePokemonIDs;
    }

    public void setShowablePokemonIDs(Set<PokemonIdOuterClass.PokemonId> ids) {
        Set<String> showablePokemonStringIDs = new HashSet<>();
        for (PokemonIdOuterClass.PokemonId pokemonId : ids) {
            showablePokemonStringIDs.add(String.valueOf(pokemonId.getNumber()));
        }
        sharedPreferences.edit().putStringSet(POKEMONS_TO_SHOW, showablePokemonStringIDs).apply();
    }

    @Override
    public void setShowMapSuggestion(boolean showMapSuggestion) {
        sharedPreferences.edit().putBoolean(SHOW_MAP_SUGGESTION, showMapSuggestion).apply();
    }

    @Override
    public boolean getShowMapSuggestion() {
        return sharedPreferences.getBoolean(SHOW_MAP_SUGGESTION, true);
    }

    @Override
    public void setServiceState(@NonNull boolean isEnabled) {
        sharedPreferences.edit().putBoolean(SERVICE_KEY, isEnabled).apply();
    }

    @Override
    public boolean isServiceEnabled() {
        return sharedPreferences.getBoolean(SERVICE_KEY, false);
    }

    @Override
    public int getServiceRefreshRate() {
        return Integer.valueOf(sharedPreferences.getString(SERVICE_REFRESH_KEY, "60"));
    }
}
