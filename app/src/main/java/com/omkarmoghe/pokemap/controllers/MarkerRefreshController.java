package com.omkarmoghe.pokemap.controllers;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.TabLayout;
import android.util.Log;

import com.omkarmoghe.pokemap.models.events.MarkerUpdateEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Rohan on 26-07-2016.
 */


public class MarkerRefreshController {

    final private String TAG = MarkerRefreshController.class.getName();

    private HandlerThread mThread;
    private Handler mHandler;
    private static final int DEFAULT_UPDATE_INTERVAL = 10 * 1000;//10 seconds : heartbeat
    private long updateInterval = DEFAULT_UPDATE_INTERVAL;
    private MarkerUpdateEvent mEvent;
    private Runnable markerExpiryUpdate;

    private static MarkerRefreshController mInstance;

    private MarkerRefreshController() {
        if (mThread == null) {

            mThread = new HandlerThread("MarkerRefreshThread");
            mThread.start();
            mHandler = new Handler(mThread.getLooper());
            mEvent = new MarkerUpdateEvent();
            markerExpiryUpdate = new Runnable() {
                @Override
                public void run()
                {
                    /**
                     * Talking to:
                     * {@link com.omkarmoghe.pokemap.views.map.MapWrapperFragment,}
                     */
                    EventBus.getDefault().post(new MarkerUpdateEvent());
                }
            };

        } else {
            throw new RuntimeException("Invalid state of Marker Refresh Thread.");
        }

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
     * Cleanup thread state.
     * Must ensure thread is properly quitting to allow safe reInit when needed
     */
    public void clear() {
        if(mThread!=null) {
            try {
                mThread.join();
                mThread.quit();            //I don't need to process the scheduled messages in queue.

            } catch (InterruptedException e) {
                Log.d(TAG, "Quitting too soon.. doing final cleanup");
            } finally {
                mThread = null;
                mInstance = null;
            }
        }
    }

    /**
     * Combo call for starting, resetting, refreshing, scheduling next update
     * Must be called separately after [@link notifyTimeToExpiry]
     */
    public void reset() {
        getInstance().mHandler.removeCallbacks(markerExpiryUpdate);
        getInstance().mHandler.postDelayed(markerExpiryUpdate, updateInterval);
        updateInterval = DEFAULT_UPDATE_INTERVAL;
    }

    /**
     * Hook to determine next closest expiration time.
     * Can be scheduled at regular intervals to use as a heartbeat.
     *
     * @param timeToExpiry
     */
    public void notifyTimeToExpiry(long timeToExpiry) {
        if (updateInterval > timeToExpiry) {
            updateInterval = timeToExpiry;
        }
    }

}
