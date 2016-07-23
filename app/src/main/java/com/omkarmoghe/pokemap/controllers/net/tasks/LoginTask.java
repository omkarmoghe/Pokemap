package com.omkarmoghe.pokemap.controllers.net.tasks;

import android.os.AsyncTask;

import com.omkarmoghe.pokemap.controllers.net.NianticManager;
import com.omkarmoghe.pokemap.models.events.IEvent;
import com.omkarmoghe.pokemap.models.events.LoginEventResult;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Jon on 7/23/2016.
 */
public class LoginTask extends AsyncTask<String, Void, IEvent> {

    private String username, password;

    public LoginTask(String username, String password) {
        this.username = username;
        this.password = password;
    }

    protected IEvent doInBackground(String... urls) {
        return NianticManager.getInstance().login(username, password);
    }

    protected void onPostExecute(IEvent result) {
        EventBus.getDefault().post(result);
    }

}