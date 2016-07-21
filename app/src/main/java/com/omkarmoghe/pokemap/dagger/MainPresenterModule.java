package com.omkarmoghe.pokemap.dagger;

import android.app.Application;

import com.omkarmoghe.pokemap.model.MainModel;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for {@link com.omkarmoghe.pokemap.presenter.MainPresenter}.
 * <p>
 * Created by fess on 21.07.16.
 */
@Module
public class MainPresenterModule {

    @Provides
    public static MainModel provideModel(Application app) {
        return new MainModel(app);
    }
}
