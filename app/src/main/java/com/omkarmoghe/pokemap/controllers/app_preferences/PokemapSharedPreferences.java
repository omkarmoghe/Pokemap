package com.omkarmoghe.pokemap.controllers.app_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

import POGOProtos.Enums.PokemonIdOuterClass;

/**
 * Provide convenience methods to access shared preferences
 */

public final class PokemapSharedPreferences implements PokemapAppPreferences {
    private static final String USERNAME_KEY = "UsernameKey";
    private static final String PASSWORD_KEY = "PasswordKey";
    private static final String GOOGLE_TOKEN_KEY = "GoogleTokenKey";
    private static final String SHOW_SCANNED_PLACES = "scanned_checkbox";
    private static final String SHOW_POKESTOPS = "pokestops_checkbox";
    private static final String SHOW_GYMS = "gyms_checkbox";
    private static final String SERVICE_KEY = "background_poke_service";
    private static final String SERVICE_REFRESH_KEY = "service_refresh_rate";
    private static final String POKEMONS_TO_SHOW = "pokemons_to_show";

    private final SharedPreferences sharedPreferences;

    public PokemapSharedPreferences(@NonNull Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public boolean isUsernameSet() {
        return sharedPreferences.contains(USERNAME_KEY);
    }

    @Override
    public boolean isPasswordSet() {
        return sharedPreferences.contains(PASSWORD_KEY);
    }

    public Set<PokemonIdOuterClass.PokemonId> getShowablePokemonIDs() {
        Set<String> showablePokemonStringIDs = sharedPreferences.getStringSet(POKEMONS_TO_SHOW, new HashSet<String>());
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
    public String getUsername() {
        return sharedPreferences.getString(USERNAME_KEY, "");
    }

    @Override
    public void setUsername(@NonNull String username) {
        sharedPreferences.edit().putString(USERNAME_KEY, username).apply();
    }

    @Override
    public void setPassword(@NonNull String password) {
        sharedPreferences.edit().putString(PASSWORD_KEY, password).apply();
    }

    @Override
    public String getPassword() {
        return sharedPreferences.getString(PASSWORD_KEY, "");
    }

    @Override
    public boolean isGoogleTokenAvailable() {
        return sharedPreferences.contains(GOOGLE_TOKEN_KEY);
    }

    @Override
    public String getGoogleToken() {
        return sharedPreferences.getString(GOOGLE_TOKEN_KEY, "");
    }

    @Override
    public void setServiceState(@NonNull boolean isEnabled) {
        sharedPreferences.edit().putBoolean(SERVICE_KEY, isEnabled).apply();
    }

    @Override
    public void setGoogleToken(@NonNull String token) {
        sharedPreferences.edit().putString(GOOGLE_TOKEN_KEY, token).apply();
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
    public boolean getShowGyms() {
        return sharedPreferences.getBoolean(SHOW_GYMS, false);
    }

    @Override
    public void clearLoginCredentials() {

        sharedPreferences.edit().remove(GOOGLE_TOKEN_KEY).apply();
        sharedPreferences.edit().remove(USERNAME_KEY).apply();
        sharedPreferences.edit().remove(PASSWORD_KEY).apply();
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
