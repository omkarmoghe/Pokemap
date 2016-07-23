package com.omkarmoghe.pokemap.controllers.net.tasks;

import android.os.AsyncTask;

import com.omkarmoghe.pokemap.controllers.net.NianticManager;
import com.omkarmoghe.pokemap.models.events.IEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Jon on 7/23/2016.
 */
public class GetCatchablePokemonTask extends AsyncTask<String, Void, IEvent> {

    private double latitude, longitude,  altitude;

    public GetCatchablePokemonTask(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    protected IEvent doInBackground(String... urls) {
        return NianticManager.getInstance().getCatchablePokemon(latitude, longitude, altitude);
    }

    protected void onPostExecute(IEvent result) {
        EventBus.getDefault().post(result);
    }

}