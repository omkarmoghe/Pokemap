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
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import POGOProtos.Enums.PokemonIdOuterClass;



public class PokemonNotificationService extends Service{

    private static final String TAG = "NotificationService";

    private static final int notificationId = 2423235;
    private static final String ACTION_STOP_SELF = "com.omkarmoghe.pokemap.STOP_SERVICE";

    public static boolean isRunning = false;

    private UpdateRunnable updateRunnable;
    private Thread workThread;
    private LocationManager locationManager;
    private NianticManager nianticManager;
    private NotificationCompat.Builder builder;
    private PokemapSharedPreferences preffs;
    private static int WAIT_BEFORE_SCAN =15;

    private static int ONE_SECOND =1000;

    private List<CatchablePokemon> pokemonFound=null;


    public PokemonNotificationService() {
        ;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        pokemonFound=new ArrayList<>();
        createNotification();

        preffs = new PokemapSharedPreferences(this);

        EventBus.getDefault().register(this);

        locationManager = LocationManager.getInstance(this);
        nianticManager = NianticManager.getInstance();

        updateRunnable = new UpdateRunnable(preffs.getServiceRefreshRate() * 1000);
        workThread = new Thread(updateRunnable);

        initBroadcastReceiver();
        workThread.start();
        locationManager.onResume();

        isRunning = true;
    }

    /**
     * This sets up the broadcast reciever.
     */
    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STOP_SELF);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        cancelNotification();
        updateRunnable.stop();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReceiver);
        isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private void createNotification(){
        builder = new NotificationCompat.Builder(getApplication())
                .setSmallIcon(R.drawable.ic_gps_fixed_white_24px)
                .setContentTitle(getString(R.string.notification_service_title))
                .setContentText(getString(R.string.notification_service_scanning, WAIT_BEFORE_SCAN)).setOngoing(true);

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        builder.setContentIntent(pi);

        Intent stopService = new Intent();
        stopService.setAction(ACTION_STOP_SELF);

        PendingIntent piStopService = PendingIntent.getBroadcast(this,0,stopService,0);
        builder.addAction(R.drawable.ic_cancel_white_24px, getString(R.string.notification_service_stop), piStopService);

        Intent pokemonGoIntent = getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
        if (pokemonGoIntent != null) { // make sure we will be able to launch Pokémon GO
            PendingIntent pokemonGoPendingIntent = PendingIntent.getActivity(this, 1, pokemonGoIntent, 0);
            builder.addAction(R.drawable.ic_pokeball, getString(R.string.notification_service_launch_pokemon_go), pokemonGoPendingIntent);
        }

        nm.notify(notificationId,builder.build());
    }

    private void cancelNotification(){
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(notificationId);
    }

    @Subscribe
    public void onEvent(CatchablePokemonEvent event) {
        if(event.isNotification()) {
            List<CatchablePokemon> catchablePokemon = event.getCatchablePokemon();

            LatLng location = locationManager.getLocation();
            Location myLoc = new Location("");
            myLoc.setLatitude(location.latitude);
            myLoc.setLongitude(location.longitude);
            builder.setContentText(getString(R.string.notification_service_pokemon_near, catchablePokemon.size()));
            builder.setStyle(null);

            if (!catchablePokemon.isEmpty()) {
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(getString(R.string.notification_service_pokemon_in_area, catchablePokemon.size()));
                Set<PokemonIdOuterClass.PokemonId> showablePokemonIDs = preffs.getShowablePokemonIDs();

                for (CatchablePokemon cp : catchablePokemon) {
                    //Only show the notification if the Pokemon is in the preference list
                    if (showablePokemonIDs.contains(cp.getPokemonId())) {

                        String pokeName = PokemonIdUtils.getLocalePokemonName(getApplicationContext(), cp.getPokemonId().name());
                        //Vibrate if a new pokemon is found
                        if (!pokemonFound.contains(cp)) {
                            System.out.println("New Pokemon found : " + pokeName);
                            vibrate();
                            pokemonFound.add(cp);
                        }
                        Location pokeLocation = new Location("");
                        pokeLocation.setLatitude(cp.getLatitude());
                        pokeLocation.setLongitude(cp.getLongitude());
                        long remainingTime = cp.getExpirationTimestampMs() - System.currentTimeMillis();
                        if (remainingTime < 0) {
                            remainingTime = -1;
                        }
                        long remTime = TimeUnit.MILLISECONDS.toMinutes(remainingTime);
                        int dist = (int) Math.ceil(pokeLocation.distanceTo(myLoc));

                        inboxStyle.addLine(getString(R.string.notification_service_inbox_line, pokeName, remTime, dist));
                    }
                }
                //Clean old pokemons
                pokemonFound.retainAll(catchablePokemon);
                builder.setStyle(inboxStyle);
            }

            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(notificationId, builder.build());
        }
    }

    private void vibrate() {
        Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }

    private final class UpdateRunnable implements Runnable{
        private long refreshRate = preffs.getServiceRefreshRate() * ONE_SECOND;
        private boolean isRunning = true;

        public UpdateRunnable(int refreshRate){
            this.refreshRate = refreshRate;
        }

        @Override
        public void run() {

                try {

                    // initial wait (fFor a reason! Do NOT remove because of cyclic sleep!)
                    Thread.sleep(WAIT_BEFORE_SCAN * ONE_SECOND);
                    Log.d(this.getClass().getSimpleName(),"SERVICE WAIT is ended");
                    while (isRunning) {
                        if(locationManager==null) {
                            locationManager = LocationManager.getInstance(PokemonNotificationService.this);
                        }

                        LatLng currentLocation = locationManager.getLocation();

                        Log.d(this.getClass().getSimpleName(),"Get CurrentLocation: "+currentLocation);
                        if (currentLocation != null){
                            Log.d(this.getClass().getSimpleName(),"Fetch Pokemon Around: "+currentLocation);
                            nianticManager.getCatchablePokemonInArea(currentLocation.latitude, currentLocation.longitude, 0,preffs.getServiceSteps(),true);
                        }

                        // cyclic sleep
                        Thread.sleep(refreshRate);

                    }
                } catch (InterruptedException | NullPointerException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed updating. UpdateRunnable.run() raised: " + e.getMessage());
                }
        }

        public void stop(){
            isRunning = false;
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
            locationManager.onPause();
        }
    };

    public static boolean isRunning() {
        return isRunning;
    }
}
