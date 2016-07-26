package com.omkarmoghe.pokemap.controllers.app_preferences;

import android.support.annotation.NonNull;

/**
 * A contract which defines a user's app preferences
 */
public interface PokemapAppPreferences {
    /**
     * @return true if the username has been set
     */
    boolean isUsernameSet();

    /**
     * @return true if password has been set
     */
    boolean isPasswordSet();

    /**
     * @return the username stored or an empty @see java.lang.String
     */
    String getUsername();

    /**
     * @param username that should be set
     */
    void setUsername(@NonNull String username);

    /**
     * @param password that should be set
     */
    void setPassword(@NonNull String password);

    /**
     * @return the password stored or an empty @see java.lang.String
     */
    String getPassword();

    boolean isGoogleTokenAvailable();

    String getGoogleToken();

    void setGoogleToken(@NonNull String token);

    boolean getShowScannedPlaces();
    boolean getShowPokestops();
    boolean getShowGyms();

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
}
