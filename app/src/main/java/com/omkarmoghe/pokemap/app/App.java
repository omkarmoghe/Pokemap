package com.omkarmoghe.pokemap.app;

import android.app.Application;

import com.omkarmoghe.pokemap.dagger.AppModule;
import com.omkarmoghe.pokemap.dagger.DaggerMainComponent;
import com.omkarmoghe.pokemap.dagger.MainActivityModule;
import com.omkarmoghe.pokemap.dagger.MainComponent;
import com.omkarmoghe.pokemap.dagger.MainPresenterModule;

/**
 * Custom application class.
 * <p>
 * Created by fess on 21.07.16.
 */
public class App extends Application {

    private MainComponent mainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mainComponent = DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .mainPresenterModule(new MainPresenterModule())
                .mainActivityModule(new MainActivityModule())
                .build();
    }

    public MainComponent getMainComponent() {
        return mainComponent;
    }
}
