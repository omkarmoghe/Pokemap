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
    private static final String SERVICE_KEY = "background_poke_service";
    private static final String SERVICE_REFRESH_KEY = "service_refresh_rate";

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
    public void setServiceState(@NonNull boolean isEnabled) {
        sharedPreferences.edit().putBoolean(SERVICE_KEY,isEnabled).apply();
    }

    @Override
    public boolean isServiceEnabled() {
        return sharedPreferences.getBoolean(SERVICE_KEY,false);
    }

    @Override
    public int getServiceRefreshRate() {
        return Integer.valueOf(sharedPreferences.getString(SERVICE_REFRESH_KEY,"60"));
    }
}
