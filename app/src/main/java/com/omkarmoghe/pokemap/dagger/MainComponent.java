package com.omkarmoghe.pokemap.dagger;

import com.omkarmoghe.pokemap.MainActivity;
import com.omkarmoghe.pokemap.rx.model.MainModel;
import com.omkarmoghe.pokemap.rx.presenter.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component for the main activity.
 * <p>
 * Created by fess on 21.07.16.
 */
@Singleton
@Component(modules = {AppModule.class, MainPresenterModule.class, MainActivityModule.class})
public interface MainComponent {

    void inject(MainModel model);

    void inject(MainActivity activity);

    void inject(MainPresenter presenter);

}
