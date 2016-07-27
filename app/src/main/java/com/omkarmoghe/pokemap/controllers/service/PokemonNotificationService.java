package com.omkarmoghe.pokemap.controllers.service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;
import com.omkarmoghe.pokemap.controllers.map.LocationManager;
import com.omkarmoghe.pokemap.controllers.net.NianticManager;
import com.omkarmoghe.pokemap.models.events.CatchablePokemonEvent;
import com.omkarmoghe.pokemap.util.PokemonIdUtils;
import com.omkarmoghe.pokemap.views.MainActivity;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.pokemon.Pokemon;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import POGOProtos.Enums.PokemonIdOuterClass;


public class PokemonNotificationService extends Service{

    private static final String TAG = "NotificationService";

    private static final int notificationId = 2423235;
    private static final String ACTION_STOP_SELF = "com.omkarmoghe.pokemap.STOP_SERVICE";
    private static final long[] VIBRATE_PATTERN = new long[]{0,100,200,100};

    private UpdateRunnable updateRunnable;
    private Thread workThread;
    private LocationManager locationManager;
    private NianticManager nianticManager;
    private NotificationCompat.Builder builder;
    private PokemapSharedPreferences preffs;
    private Vibrator vibratorManager;
    private boolean vibrate;

    private List<PokemonIdOuterClass.PokemonId> previousFoundPokemon;

    public PokemonNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        EventBus.getDefault().register(this);
        createNotification();

        preffs = new PokemapSharedPreferences(this);
        vibrate = preffs.isServiceVibrationEnabled();
        vibrate =  ContextCompat.checkSelfPermission(this,
                Manifest.permission.VIBRATE) ==  PackageManager.PERMISSION_GRANTED;

        if(vibrate){
            vibratorManager = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrate = vibratorManager.hasVibrator();
        }

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
        builder.addAction(R.drawable.ic_cancel_black_24px, getString(R.string.notification_service_stop), piStopService);

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
        builder.setStyle(null);

        List<PokemonIdOuterClass.PokemonId> currentFoundSet = new LinkedList<>();
        if(!catchablePokemon.isEmpty()){
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            Set<PokemonIdOuterClass.PokemonId> showablePokemonIDs = preffs.getShowablePokemonIDs();
            
            for(CatchablePokemon cp : catchablePokemon){
                //Only show the notification if the Pokemon is in the preference list
                if(showablePokemonIDs.contains(cp.getPokemonId())) {
                    Location pokeLocation = new Location("");
                    PokemonIdOuterClass.PokemonId pokemonId = cp.getPokemonId();
                    pokeLocation.setLatitude(cp.getLatitude());
                    pokeLocation.setLongitude(cp.getLongitude());
                    long remainingTime = cp.getExpirationTimestampMs() - System.currentTimeMillis();
                    currentFoundSet.add(pokemonId);
                    String pokeName = PokemonIdUtils.getLocalePokemonName(getApplicationContext(),pokemonId.name());
                    long remTime = TimeUnit.MILLISECONDS.toMinutes(remainingTime);
                    int dist = (int)Math.ceil(pokeLocation.distanceTo(myLoc));
                    inboxStyle.addLine(getString(R.string.notification_service_inbox_line, pokeName, remTime,dist));
                }
            }
            builder.setStyle(inboxStyle);
            inboxStyle.setBigContentTitle(getString(R.string.notification_service_pokemon_in_area, currentFoundSet.size()));
        }
        builder.setContentText(getString(R.string.notification_service_pokemon_near,currentFoundSet.size()));

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(notificationId,builder.build());
        if(vibrate){
            if(previousFoundPokemon != null && !previousFoundPokemon.equals(currentFoundSet)){
                vibratorManager.vibrate(VIBRATE_PATTERN,-1);
            }
            previousFoundPokemon = currentFoundSet;
        }
    }

    private final class UpdateRunnable implements Runnable{
        private long refreshRate ;
        private boolean isRunning = true;

        public UpdateRunnable(int refreshRate){
            this.refreshRate = refreshRate * 1000;
        }

        @Override
        public void run() {
            while(isRunning){
                try{
                    LatLng currentLocation = locationManager.getLocation();

                    if(currentLocation != null){
                        nianticManager.getCatchablePokemon(currentLocation.latitude,currentLocation.longitude,0);
                    }else {
                        locationManager = LocationManager.getInstance(PokemonNotificationService.this);
                    }
                    Thread.sleep(refreshRate);

                } catch (InterruptedException | NullPointerException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed updating. UpdateRunnable.run() raised: " + e.getMessage());
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
}
