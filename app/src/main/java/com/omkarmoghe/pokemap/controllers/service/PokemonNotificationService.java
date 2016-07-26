package com.omkarmoghe.pokemap.controllers.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;
import com.omkarmoghe.pokemap.controllers.map.LocationManager;
import com.omkarmoghe.pokemap.controllers.net.NianticManager;
import com.omkarmoghe.pokemap.models.events.CatchablePokemonEvent;
import com.omkarmoghe.pokemap.views.MainActivity;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class PokemonNotificationService extends Service{
    private static final int notificationId = 2423235;
    private static final String ACTION_STOP_SELF = "com.omkarmoghe.pokemap.STOP_SERVICE";

    private UpdateRunnable updateRunnable;
    private Thread workThread;
    private LocationManager locationManager;
    private NianticManager nianticManager;
    private NotificationCompat.Builder builder;
    private PokemapSharedPreferences preffs;


    public PokemonNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("PokeMap","Service.onCreate()");
        EventBus.getDefault().register(this);
        createNotification();

        preffs = new PokemapSharedPreferences(this);

        locationManager = LocationManager.getInstance(this);
        nianticManager = NianticManager.getInstance();

        updateRunnable = new UpdateRunnable(preffs.getServiceRefreshRate());
        workThread = new Thread(updateRunnable);

        initBroadcastReciever();
        workThread.start();
        locationManager.onResume();

    }

    /**
     * This sets up the broadcast reciever.
     */
    private void initBroadcastReciever() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STOP_SELF);
        registerReceiver(mBroadcastReciever,intentFilter);
    }

    @Override
    public void onDestroy() {
        cancelNotification();
        updateRunnable.stop();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReciever);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private void createNotification(){
        builder = new NotificationCompat.Builder(getApplication())
                .setSmallIcon(R.drawable.ic_gps_fixed_white_24px)
                .setContentTitle(getString(R.string.notification_service_title))
                .setContentText(getString(R.string.notification_service_scanning)).setOngoing(true);

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //builder.setContentIntent(pi);

        Intent stopService = new Intent();
        stopService.setAction(ACTION_STOP_SELF);

        PendingIntent piStopService = PendingIntent.getBroadcast(this,0,stopService,0);
        builder.addAction(R.drawable.ic_cancel_black_24px,"Stop Service",piStopService);

        nm.notify(notificationId,builder.build());
    }

    private void cancelNotification(){
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(notificationId);
    }

    @Subscribe
    public void onEvent(CatchablePokemonEvent event) {
        List<CatchablePokemon> catchablePokemon = event.getCatchablePokemon();

        LatLng location = locationManager.getLocation();
        Location myLoc = new Location("");
        myLoc.setLatitude(location.latitude);
        myLoc.setLongitude(location.longitude);
        builder.setContentText(catchablePokemon.size() + " "+getString(R.string.notification_service_pokemon_nearby)+".");
        builder.setStyle(null);

        if(!catchablePokemon.isEmpty()){
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(catchablePokemon.size() + " "+getString(R.string.notification_service_pokemon_nearby)+":");
            for(CatchablePokemon cp : catchablePokemon){
                Location pokeLocation = new Location("");
                pokeLocation.setLatitude(cp.getLatitude());
                pokeLocation.setLongitude(cp.getLongitude());
                long remainingTime = cp.getExpirationTimestampMs() - System.currentTimeMillis();
                inboxStyle.addLine(getLocalePokemonName(cp.getPokemonId().name()) + "(" +
                        TimeUnit.MILLISECONDS.toMinutes(remainingTime) +
                        " "+getString(R.string.notification_minutes)+"," + Math.ceil(pokeLocation.distanceTo(myLoc)) + " "+getString(R.string.notification_meters)+")");
            }

            builder.setStyle(inboxStyle);
        }

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(notificationId,builder.build());
    }

    private final class UpdateRunnable implements Runnable{
        private long refreshRate = preffs.getServiceRefreshRate() * 1000;
        private boolean isRunning = true;

        public UpdateRunnable(int refreshRate){
            this.refreshRate = refreshRate;
        }

        @Override
        public void run() {
            while(isRunning){
                try{
                    LatLng currentLocation = locationManager.getLocation();

                    if(currentLocation != null){
                        nianticManager.getMapInformation(currentLocation.latitude,currentLocation.longitude,0);
                    }else {
                        locationManager = LocationManager.getInstance(PokemonNotificationService.this);
                    }
                    Thread.sleep(refreshRate);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        public void stop(){
            isRunning = false;
        }
    }

    private BroadcastReceiver mBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
            locationManager.onPause();
        }
    };

    /**
     * try to resolve PokemonName from Resources
     * @param apiPokeName
     * @return
     */
    private String getLocalePokemonName(String apiPokeName){
        int resId = 0;
        try{
            Class resClass = R.string.class;
            Field field = resClass.getField(apiPokeName.toLowerCase());
            resId = field.getInt(null);
        }catch(Exception e){
            com.pokegoapi.util.Log.e("PokemonTranslation","Failure to get Name",e);
            resId = -1;
        }
        return resId > 0 ? getString(resId) : apiPokeName;
    }
}
