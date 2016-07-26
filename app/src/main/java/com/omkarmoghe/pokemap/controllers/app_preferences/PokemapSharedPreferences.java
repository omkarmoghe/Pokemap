package com.omkarmoghe.pokemap.controllers.app_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

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


}
