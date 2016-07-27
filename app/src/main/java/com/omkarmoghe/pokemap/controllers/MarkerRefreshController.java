package com.omkarmoghe.pokemap.controllers;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.omkarmoghe.pokemap.models.events.MarkerExpired;
import com.omkarmoghe.pokemap.models.events.MarkerUpdate;
import com.omkarmoghe.pokemap.models.map.PokemonMarkerExtended;

import org.greenrobot.eventbus.EventBus;

import java.sql.Time;

/**
 * Created by Rohan on 26-07-2016.
 */


public class MarkerRefreshController {

    final private String TAG = MarkerRefreshController.class.getName();

    private static final int DEFAULT_UPDATE_INTERVAL = 1000;//1 seconds : heartbeat
    private static final int MARKER_EXPIRED = 1;

    private Handler mHandler;
    private CountDownTimer mTimer;

    private static MarkerRefreshController mInstance;

    private MarkerRefreshController() {
        Handler.Callback callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MARKER_EXPIRED:
                        //If Marker Expired
                        if (message.obj instanceof PokemonMarkerExtended) {
                            EventBus.getDefault().post(new MarkerExpired((PokemonMarkerExtended) message.obj));
                            return true;
                        }
                        break;
                }
                return false;
            }
        };
        mHandler = new Handler(callback);
    }

    /*
     * Singleton getter
     */
    public static MarkerRefreshController getInstance() {
        if (mInstance == null) {
            mInstance = new MarkerRefreshController();
        }
        return mInstance;
    }


    /**
     * Cleanup Messages and cancels the timer if it is running.
     */
    public void clear() {
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
        mHandler.removeMessages(MARKER_EXPIRED);

    }

    public void startTimer(long duration){
        if(mTimer != null){
            mTimer.cancel();
        }

        if(duration <= 0) {
            return;
        }

        final MarkerUpdate event = new MarkerUpdate();
        mTimer = new CountDownTimer(duration, DEFAULT_UPDATE_INTERVAL) {
            @Override
            public void onTick(long l) {
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFinish() {
                mTimer = null;
                EventBus.getDefault().post(event);
            }
        };
        mTimer.start();
    }

    public void stopTimer(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void clearMessages(){
        mHandler.removeMessages(MARKER_EXPIRED);
    }

    public void postMarker(PokemonMarkerExtended markerData){
        long time = markerData.getCatchablePokemon().getExpirationTimestampMs() - System.currentTimeMillis();
        if(time > 0) {
            Message message = mHandler.obtainMessage(MARKER_EXPIRED, markerData);
            mHandler.sendMessageDelayed(message, time);
        }
    }
}
