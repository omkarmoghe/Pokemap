package com.omkarmoghe.pokemap.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.common.BaseActivity;
import com.omkarmoghe.pokemap.map.MapWrapperFragment;
import com.omkarmoghe.pokemap.settings.SettingsActivity;

import java.lang.annotation.Retention;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class MainActivity extends BaseActivity implements OnMapReadyCallback{

    public static final String TAG = "Pokemap";

    private static final String ARG_TOKEN = "Auth Token";
    private static final String ARG_PROVIDER = "Token Provider";

    public static final String PROVIDER_GOOGLE = "google";
    public static final String PROVIDER_PTC = "PTC";

    @Retention(SOURCE)
    @StringDef({
            PROVIDER_GOOGLE,
            PROVIDER_PTC
    })
    @interface AuthProviders{}

    // fragments
    private MapWrapperFragment mMapWrapperFragment;
    private AuthInfo mAuthInfo;
    private String mAuthToken;
    @AuthProviders
    private String mAuthProvider;

    // Preferences
    SharedPreferences pref;

    public static void start(Activity caller){
        Intent intent = new Intent(caller, MainActivity.class);
        caller.startActivity(intent);
    }

    public static void start(LoginActivity caller, String token, @AuthProviders String provider){
        Intent intent = new Intent(caller, MainActivity.class);
        intent.putExtra(ARG_TOKEN, token);
        intent.putExtra(ARG_PROVIDER, provider);
        caller.startActivity(intent);
        caller.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(this);


        Intent intent = getIntent();
        if(intent.hasExtra(ARG_TOKEN) && intent.hasExtra(ARG_PROVIDER)){
            if(intent.getStringExtra(ARG_PROVIDER).equals(PROVIDER_GOOGLE)){
                mAuthProvider = PROVIDER_GOOGLE;
            }else{
                mAuthProvider = PROVIDER_PTC;
            }

            mAuthToken = intent.getStringExtra(ARG_TOKEN);

            mAuthInfo = createAuthInfo(mAuthToken, mAuthProvider);
        }else if(savedInstanceState.containsKey(ARG_TOKEN) && savedInstanceState.containsKey(ARG_PROVIDER)){
            if(savedInstanceState.getString(ARG_PROVIDER).equals(PROVIDER_GOOGLE)){
                mAuthProvider = PROVIDER_GOOGLE;
            }else{
                mAuthProvider = PROVIDER_PTC;
            }

            mAuthToken = savedInstanceState.getString(ARG_TOKEN);

            mAuthInfo = createAuthInfo(mAuthToken, mAuthProvider);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMapWrapperFragment = MapWrapperFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, mMapWrapperFragment)
                   .addToBackStack(null)
                   .commit();

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(ARG_TOKEN, mAuthToken);
        outState.putString(ARG_PROVIDER, mAuthProvider);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_TOKEN, mAuthToken);
        outState.putString(ARG_PROVIDER, mAuthProvider);
        super.onSaveInstanceState(outState);
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
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // TODO: test all this shit on a 6.0+ phone lmfao
        switch (requestCode) {
            case 703:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission granted");
                }
                break;
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static AuthInfo createAuthInfo(String token, @AuthProviders String provider){
        AuthInfo.Builder authbuilder = AuthInfo.newBuilder();
        authbuilder.setProvider(provider);
        authbuilder.setToken(AuthInfo.JWT.newBuilder().setContents(token).setUnknown2(59).build());

        return authbuilder.build();
    }
}
