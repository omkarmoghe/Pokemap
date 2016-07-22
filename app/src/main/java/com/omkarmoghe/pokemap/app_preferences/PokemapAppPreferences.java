package com.omkarmoghe.pokemap.app_preferences;

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
     * Remove username associated with account.
     */
    void removeUsername();

    /**
     * @param password that should be set
     */
    void setPassword(@NonNull String password);

    /**
     * @return the password stored or an empty @see java.lang.String
     */
    String getPassword();


    /**
     * Remove password associated with account
     */
    void removePassword();
}
