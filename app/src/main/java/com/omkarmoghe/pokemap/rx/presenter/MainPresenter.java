package com.omkarmoghe.pokemap.rx.presenter;

import android.content.Context;

import com.omkarmoghe.pokemap.MainActivity;
import com.omkarmoghe.pokemap.rx.model.MainModel;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Presenter part of the MVP pattern for {@link com.omkarmoghe.pokemap.MainActivity}.
 * <p>
 * Created by fess on 21.07.16.
 */
public class MainPresenter extends BasePresenter<MainActivity> {

    @Inject
    protected MainModel model;

    public Observable<String> getToken(Context context,
                                       String login,
                                       String password) {
        return model.getTokenObs(context, login, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}