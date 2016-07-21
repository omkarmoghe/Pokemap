package com.omkarmoghe.pokemap;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.protobuf.ByteString;
import com.omkarmoghe.pokemap.app.App;
import com.omkarmoghe.pokemap.map.MapWrapperFragment;
import com.omkarmoghe.pokemap.presenter.MainPresenter;
import com.omkarmoghe.pokemap.protobuf.PokemonOuterClass.RequestEnvelop;
import com.omkarmoghe.pokemap.settings.SettingsActivity;

import javax.inject.Inject;

import nucleus.factory.PresenterFactory;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(MainPresenter.class)
public class MainActivity extends NucleusAppCompatActivity<MainPresenter>
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MapWrapperFragment.LocationRequestListener {

    public static final String TAG = "Pokemap";

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;

    // Niantic token
    private String token;

    // fragments
    private MapWrapperFragment mMapWrapperFragment;

    // Google api shit
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    // Preferences
    @Inject
    protected SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final PresenterFactory<MainPresenter> superFactory = super.getPresenterFactory();
        setPresenterFactory(() -> {
            MainPresenter presenter = superFactory.createPresenter();
            ((App) getApplication()).getMainComponent().inject(presenter);
            return presenter;
        });

        super.onCreate(savedInstanceState);
        ((App) getApplication()).getMainComponent().inject(this);

        setContentView(R.layout.activity_main);

        initActionBar();
        setUpGoogleApiClient();
        initMapFragment();
    }

    private void initMapFragment() {
        mMapWrapperFragment = MapWrapperFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, mMapWrapperFragment)
                .addToBackStack(null)
                .commit();
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Needs to be translated from the Python library.
     * See https://github.com/AHAAAAAAA/PokemonGo-Map
     * See https://github.com/AHAAAAAAA/PokemonGo-Map/blob/master/example.py#L378
     */
    private void getPokemon() {
        try {
            RequestEnvelop.Requests.Builder m4 = RequestEnvelop.Requests.newBuilder();
            RequestEnvelop.MessageSingleInt.Builder msi = RequestEnvelop.MessageSingleInt.newBuilder();
            msi.setF1(System.currentTimeMillis());
            m4.setMessage(msi.build().toByteString());

            RequestEnvelop.Requests.Builder m5 = RequestEnvelop.Requests.newBuilder();
            RequestEnvelop.MessageSingleString.Builder mss = RequestEnvelop.MessageSingleString.newBuilder();
            mss.setBytes(ByteString.copyFrom("05daf51635c82611d1aac95c0b051d3ec088a930", "UTF-8"));
            m5.setMessage(mss.build().toByteString());
            // TODO: walk = sorted(getNeighbors())
            RequestEnvelop.Requests.Builder m1 = RequestEnvelop.Requests.newBuilder();
            m1.setType(106); // magic number;
            RequestEnvelop.MessageQuad.Builder mq = RequestEnvelop.MessageQuad.newBuilder();
            // TODO: mq.f1 = ''.join(map(encode, walk))
            mq.setF2(ByteString.copyFrom("\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000", "UTF-8"));

            requestLocation();
            mq.setLat(((long) mLastLocation.getLatitude()));
            mq.setLong(((long) mLastLocation.getLongitude()));

            m1.setMessage(mq.build().toByteString());

            // TODO: response = get_profile(...)...
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void login() {
        String username = pref.getString(getString(R.string.pref_username), "");
        String password = pref.getString(getString(R.string.pref_password), "");
        getPresenter().getToken(getApplicationContext(), username, password)
                .subscribe(s -> {
                    Toast.makeText(MainActivity.this, "Got token: " + s, Toast.LENGTH_SHORT).show();
                });
    }

    private void setUpGoogleApiClient() {
        if (mGoogleApiClient == null) mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /// GOOGLE FUSED LOCATION API CALLBACKS ///

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /// INTERFACES ///

    @Override
    public Location requestLocation() {
        if (mLastLocation == null) {
            setUpGoogleApiClient();
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        return mLastLocation;
    }
}
