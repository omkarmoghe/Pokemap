package com.omkarmoghe.pokemap.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * ToDo: Explain the scope of this class
 */

public final class PokemapSharedPreferences {
    private final String USERNAME_KEY = "UsernameKey";
    private final String PASSWORD_KEY = "PasswordKey";

    private final SharedPreferences sharedPreferences;

    public PokemapSharedPreferences(@NonNull Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isUsernameSet() {
        return sharedPreferences.contains(USERNAME_KEY);
    }

    public boolean isPasswordSet() {
        return sharedPreferences.contains(PASSWORD_KEY);
    }


    public String getUsername() {
        return sharedPreferences.getString(USERNAME_KEY, "");
    }

    public void setUsername(@NonNull String username) {
        sharedPreferences.edit().putString(USERNAME_KEY, username).apply();
    }

    public void setPassword(@NonNull String password) {
        sharedPreferences.edit().putString(PASSWORD_KEY, password).apply();
    }

    public String getPassword() {
        return sharedPreferences.getString(PASSWORD_KEY, "");
    }
}
