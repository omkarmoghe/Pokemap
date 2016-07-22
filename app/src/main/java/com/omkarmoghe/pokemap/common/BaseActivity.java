package com.omkarmoghe.pokemap.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.omkarmoghe.pokemap.map.LocationManager;
import com.omkarmoghe.pokemap.network.NianticManager;
import com.omkarmoghe.pokemap.presenter.BasePresenter;

import nucleus.presenter.Presenter;
import nucleus.view.NucleusAppCompatActivity;

/**
 * Created by vanshilshah on 19/07/16.
 */
public class BaseActivity<P extends Presenter> extends NucleusAppCompatActivity<P> {

    public static final String TAG = "BaseActivity";

    protected LocationManager.Listener locationListener;

    // TODO: 21.07.16 inject these with Dagger
    LocationManager locationManager;

    protected NianticManager nianticManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = LocationManager.getInstance(this);
        nianticManager = NianticManager.getInstance(this);
    }

    @Override
    public void onResume(){
        super.onResume();

        locationManager.onResume();
        if(locationListener != null){
            locationManager.register(locationListener);
        }
    }

    @Override
    public void onPause(){
        LocationManager.getInstance(this).onPause();

        if(locationListener != null){
            locationManager.unregister(locationListener);
        }
        super.onPause();
    }
}
