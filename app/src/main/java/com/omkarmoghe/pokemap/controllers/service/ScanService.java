package com.omkarmoghe.pokemap.controllers.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.controllers.map.LocationManager;
import com.omkarmoghe.pokemap.controllers.net.NianticManager;
import com.omkarmoghe.pokemap.models.events.CatchablePokemonEvent;
import com.omkarmoghe.pokemap.views.MainActivity;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class ScanService extends Service{
    private static final int notificationId = 2423235;

    private WorkRunnable workRunnable;
    private Thread workThread;
    private LocationManager locationManager;
    private NianticManager nianticManager;
    private NotificationCompat.Builder builder;


    public ScanService() {
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
        locationManager = LocationManager.getInstance(this);
        nianticManager = NianticManager.getInstance();

        workRunnable = new WorkRunnable();
        workThread = new Thread(workRunnable);


    }

    @Override
    public void onDestroy() {
        Log.d("PokeMap","Service.onDestroy()");
        cancelNotification();
        workRunnable.stop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("PokeMap","Service.onStart()");

        if(!workThread.isAlive()){
            workThread.start();
        }
        return START_STICKY;
    }

    private void createNotification(){
        builder = new NotificationCompat.Builder(getApplication())
                .setSmallIcon(R.drawable.p1)
                .setContentTitle("Pokemon Service")
                .setContentText("Scanning").setOngoing(true);

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        builder.setContentIntent(pi);

        nm.notify(notificationId,builder.build());
    }

    private void cancelNotification(){
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(notificationId);
    }

    @Subscribe
    public void onEvent(CatchablePokemonEvent event) {
        Log.d("PokeMap","Service.onEvent(CatchablePokemonEvent) "
                + event.getCatchablePokemon().size() + " pokemans");
        List<CatchablePokemon> catchablePokemon = event.getCatchablePokemon();

        LatLng location = locationManager.getLocation();
        Location myLoc = new Location("");
        myLoc.setLatitude(location.latitude);
        myLoc.setLongitude(location.longitude);

        if(catchablePokemon.isEmpty()){
            builder.setContentText("No pokemon nearby");
        }else{
            StringBuilder sb = new StringBuilder();

            for(CatchablePokemon cp : catchablePokemon){
                Location pokeLocation = new Location("");
                pokeLocation.setLatitude(cp.getLatitude());
                pokeLocation.setLongitude(cp.getLongitude());

                long remainingTime = cp.getExpirationTimestampMs() - System.currentTimeMillis();

                sb.append(cp.getPokemonId().name() + "(" +
                        TimeUnit.MILLISECONDS.toMinutes(remainingTime) +
                        " minutes," + Math.ceil(pokeLocation.distanceTo(myLoc)) + " meters)");
            }
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(catchablePokemon.size() + " pokemon nearby.");
            bigTextStyle.bigText(sb.toString());

            builder.setStyle(bigTextStyle);
            builder.setContentText(catchablePokemon.size() + " pokemon nearby.");

        }

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(notificationId,builder.build());
    }

    private final class WorkRunnable implements Runnable{
        private long sleepInMs = 1000;
        private boolean isRunning = true;

        @Override
        public void run() {
            Log.d("PokeMap","WorkRunnable.run)");

            while(isRunning){
                Log.d("PokeMap","WorkRunnable.run.loop)");
                try{
                    LatLng currentLocation = locationManager.getLocation();
                    nianticManager.getCatchablePokemon(currentLocation.latitude,currentLocation.longitude,0);

                    Thread.sleep(sleepInMs);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            Log.d("PokeMap","WorkRunnable.run stopping)");
        }

        public void stop(){
            isRunning = false;
        }
    }
}
