package com.omkarmoghe.pokemap.controllers.app_preferences;

import android.support.annotation.NonNull;

import com.omkarmoghe.pokemap.models.login.LoginInfo;

import java.util.List;
import java.util.Set;

import POGOProtos.Enums.PokemonIdOuterClass;

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
    boolean getShowLuredPokemon();
    int getSteps();

    void clearLoginCredentials();
    /**
     *
     * @param isEnabled Sets if the background service is enabled.
     */
    void setServiceState(@NonNull boolean isEnabled);

    /**
     *
     * @return Returns service state as set in preffs
     */
    boolean isServiceEnabled();

    int getServiceRefreshRate();

    /**
     * @return a set of pokemonIDs which can be shown according to the preferences.
     */
    Set<PokemonIdOuterClass.PokemonId> getShowablePokemonIDs();

    void setShowablePokemonIDs(Set<PokemonIdOuterClass.PokemonId> pokemonIDs);

    void setShowMapSuggestion(boolean showMapSuggestion);

    boolean getShowMapSuggestion();
}
