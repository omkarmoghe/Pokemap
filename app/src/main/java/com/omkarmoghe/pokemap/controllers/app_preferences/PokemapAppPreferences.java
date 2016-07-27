package com.omkarmoghe.pokemap.controllers.app_preferences;

import android.support.annotation.NonNull;

import com.omkarmoghe.pokemap.models.login.LoginInfo;

/**
 * A contract which defines a user's app preferences
 */
public interface PokemapAppPreferences {

    LoginInfo getLoginInfo();

    void setLoginInfo(LoginInfo loginInfo);

    boolean isLoggedIn();

    boolean getShowScannedPlaces();
    boolean getShowPokestops();
    boolean getShowGyms();

    void clearLoginCredentials();
}
