package com.omkarmoghe.pokemap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.omkarmoghe.pokemap.common.BaseActivity;
import com.omkarmoghe.pokemap.common.SimplePokemon;
import com.omkarmoghe.pokemap.map.LocationManager;
import com.omkarmoghe.pokemap.settings.SettingsActivity;

import java.util.List;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    public static final String TAG = "Pokemap";

    private GoogleMap map = null;
    private SupportMapFragment mapFragment;

    private boolean firstLocation;
    private LatLng myLatLng = null;
    Marker myLocation;

    // Preferences
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        firstLocation = true;
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationListener = new LocationManager.Listener() {
            @Override
            public void onLocationChanged(Location location) {
                myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "location ready");
                initWhenReady();

            }
        };

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMarkerClickListener(this);
        Log.d(TAG, "map ready");
        initWhenReady();
    }

    private void initWhenReady(){
        if(map != null && myLatLng != null){
            login();
            if(firstLocation){
                myLocation = map.addMarker(new MarkerOptions().position(myLatLng));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 12));
                firstLocation = false;
            }
        }
    }

    private void renderMarkers(List<SimplePokemon> pokemonList){
        for(SimplePokemon pokemon: pokemonList){

            int resourceID = getResources().getIdentifier("" + pokemon.getID(), "drawable", getPackageName());

            map.addMarker(new MarkerOptions()
                    .position(pokemon.getLatLng())
                    .icon(BitmapDescriptorFactory.fromResource(resourceID))
                    .snippet(pokemon.getName()));
        }
    }

    private void login() {
        String username = pref.getString(getString(R.string.pref_username), "");
        String password = pref.getString(getString(R.string.pref_password), "");
        Log.d(TAG, "Username: " + username);
        nianticManager.login(username, password, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_relogin) {
            login();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
