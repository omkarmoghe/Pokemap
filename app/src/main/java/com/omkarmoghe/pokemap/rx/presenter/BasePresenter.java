package com.omkarmoghe.pokemap.rx.presenter;

import android.os.Bundle;

import icepick.Icepick;
import nucleus.presenter.RxPresenter;

/**
 * Base for all presenters.
 * <p>
 * Created by fess on 21.07.16.
 */
public class BasePresenter<ViewType> extends RxPresenter<ViewType> {

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Icepick.restoreInstanceState(this, savedState);
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        Icepick.saveInstanceState(this, state);
    }
}