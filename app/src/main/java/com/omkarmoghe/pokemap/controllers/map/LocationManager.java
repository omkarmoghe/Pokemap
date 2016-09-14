package com.omkarmoghe.pokemap.controllers.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vanshilshah on 19/07/16.
 */
public class LocationManager {

    private static final String TAG = "LocationManager";
    private List<Listener> listeners;

    GoogleApiClient mGoogleApiClient;

    private static LocationManager instance;
    public boolean pingLocation = false;

    Location location;

    public static LocationManager getInstance(Context context) {
        if (instance == null) {
            instance = new LocationManager(context);
        }

        return instance;
    }
    private LocationManager(final Context context) {
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location newLocation) {
                location = newLocation;
                Log.d(TAG, "User location found: " + location.getLatitude() + ", " + location.getLongitude());
                notifyLocationChanged(location);
            }
        };
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            LocationRequest mLocationRequest = new LocationRequest();
                            mLocationRequest.setInterval(10000);
                            mLocationRequest.setFastestInterval(5000);
                            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                if (context instanceof Activity) {
                                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

                                }
                                return;
                            }

                            LocationServices.FusedLocationApi.requestLocationUpdates(
                                    mGoogleApiClient, mLocationRequest, locationListener);

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.e(TAG, "Failed to fetch user location. Connection suspended, code: " + i);
                            notifyLocationFetchFailed(null);
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.e(TAG, "Failed to fetch user location. Connection result: " + connectionResult.getErrorMessage());
                            notifyLocationFetchFailed(connectionResult);
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }
        listeners = new ArrayList<>();
    }

    public LatLng getLocation(){
        //Don't getLatitude without checking if location is not null... it will throw sys err...
        if(location != null){
            return new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            notifyLocationFetchFailed(null);
        }
        return null;
    }

    public void onResume(){

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void onPause(){

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public void unregister(Listener listener){
        if(listeners != null && listeners.indexOf(listener) != -1){
            listeners.remove(listener);
        }
    }
    public void register(Listener listener){
        if(listeners != null && listeners.indexOf(listener) == -1){
            listeners.add(listener);
        }
    }

    private void notifyLocationFetchFailed(@Nullable ConnectionResult connectionResult) {

        if (listeners != null) {

            for (Listener listener : listeners) {
                listener.onLocationFetchFailed(connectionResult);
            }
        }
    }

    private void notifyLocationChanged(Location location){

        if (listeners != null) {
            for (Listener listener : listeners) {
                listener.onLocationChanged(location);
            }
        }

    }

    public interface Listener {
        void onLocationChanged(Location location);
        void onLocationFetchFailed(@Nullable ConnectionResult connectionResult);
    }


}
