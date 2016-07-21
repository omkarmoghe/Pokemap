package com.omkarmoghe.pokemap.dagger;

import com.omkarmoghe.pokemap.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component for the main activity.
 * <p>
 * Created by fess on 21.07.16.
 */
@Singleton
@Component(modules = {AppModule.class, MainActivityModule.class})
public interface MainComponent {

    void inject(MainActivity activity);

}
