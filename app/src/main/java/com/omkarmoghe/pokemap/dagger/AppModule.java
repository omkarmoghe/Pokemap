package com.omkarmoghe.pokemap.dagger;

import android.app.Application;

import com.omkarmoghe.pokemap.app.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Application injection module.
 * <p>
 * Created by fess on 21.07.16.
 */
@Module
public class AppModule {

    App mApp;

    public AppModule(Application application) {
        mApp = (App) application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApp;
    }
}