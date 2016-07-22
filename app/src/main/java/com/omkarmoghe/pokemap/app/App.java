package com.omkarmoghe.pokemap.app;

import android.app.Application;

import com.omkarmoghe.pokemap.dagger.AppModule;
import com.omkarmoghe.pokemap.dagger.DaggerMainComponent;
import com.omkarmoghe.pokemap.dagger.MainActivityModule;
import com.omkarmoghe.pokemap.dagger.MainComponent;

/**
 * Custom application class.
 * <p>
 * Created by fess on 21.07.16.
 */
public class App extends Application {

    private static MainComponent mainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mainComponent = DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .mainActivityModule(new MainActivityModule())
                .build();
    }

    public static MainComponent getMainComponent() {
        return mainComponent;
    }
}
