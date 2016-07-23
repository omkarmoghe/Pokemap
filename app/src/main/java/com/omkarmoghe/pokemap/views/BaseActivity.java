package com.omkarmoghe.pokemap.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.omkarmoghe.pokemap.controllers.map.LocationManager;
import com.omkarmoghe.pokemap.controllers.net.NianticManager;

/**
 * Created by vanshilshah on 19/07/16.
 */
public class BaseActivity extends AppCompatActivity {
    public static final String TAG = "BaseActivity";
    protected LocationManager.Listener locationListener;
    LocationManager locationManager;
    protected NianticManager nianticManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = LocationManager.getInstance(this);
        nianticManager = NianticManager.getInstance();

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
