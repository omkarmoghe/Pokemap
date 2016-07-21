package com.omkarmoghe.pokemap.dagger;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for {@link com.omkarmoghe.pokemap.MainActivity}.
 * <p>
 * Created by fess on 21.07.16.
 */
@Module
public class MainActivityModule {

    @Provides
    public SharedPreferences providePrefs(Application app) {
        return PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());
    }
}
