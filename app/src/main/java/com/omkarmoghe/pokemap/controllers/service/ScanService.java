package com.omkarmoghe.pokemap.controllers.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ScanService extends Service {
    public ScanService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
