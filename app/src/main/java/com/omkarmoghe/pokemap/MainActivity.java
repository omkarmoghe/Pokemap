package com.omkarmoghe.pokemap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.protobuf.ByteString;
import com.omkarmoghe.pokemap.map.MapWrapperFragment;
import com.omkarmoghe.pokemap.protobuf.PokemonOuterClass.RequestEnvelop;
import com.omkarmoghe.pokemap.settings.SettingsActivity;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.Pokemon.CatchablePokemon;
import com.pokegoapi.auth.PTCLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
                                                               GoogleApiClient.OnConnectionFailedListener,
                                                               MapWrapperFragment.LocationRequestListener,
                        Button.OnClickListener{

    public static final String TAG = "Pokemap";
    private static Button getPoke;
    private static PokemonGo pokemonGo=null;
    private static final float GRANULARITY = 0.0008f;
    private static final float SEARCH_RANGE = 0.005f;

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    // magic constants fml
    private static final String LOGIN_URL = "https://sso.pokemon.com/sso/login?service=https://sso.pokemon.com/sso/oauth2.0/callbackAuthorize";
    private static final String LOGIN_OAUTH = "https://sso.pokemon.com/sso/oauth2.0/accessToken";
    private static final String PTC_CLIENT_SECRET = "w8ScCUXJQc6kXKw8FiOhd8Fixzht18Dq3PEVkUCP5ZPxtgyWsbTvWHFLm2wNY0JR";

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    public static final String CLIENT_ID = "mobile-app_pokemon-go";
    public static final String REDIRECT_URI = "https://www.nianticlabs.com/pokemongo/error";

    // Niantic token
    private String token;

    // fragments
    private MapWrapperFragment mMapWrapperFragment;

    // Google api shit
    GoogleApiClient mGoogleApiClient;
    Location        mLastLocation;

    // Preferences
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getPoke = (Button)findViewById(R.id.button2);
        getPoke.setOnClickListener(this);

        setUpGoogleApiClient();

        mMapWrapperFragment = MapWrapperFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, mMapWrapperFragment)
                   .addToBackStack(null)
                   .commit();

        try {
            getToken("", "");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })
                .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext())))
                .followRedirects(false)
                .followSslRedirects(false)
                .build();

                //new GoogleLogin(httpClient).login("token");
        /*AsyncTask<Object,Object,Object>asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo auth= new PTCLogin(httpClient).login(token);
                pokemonGo = new PokemonGo(auth,httpClient);
                Log.d("Profile Info: ",pokemonGo.getPlayerProfile().toString());
                return null;
            }
        };
        asyncTask.execute();*/


        //getPokemon();
    }

    /**
     * This definitely needs to be cleaned up a little but yeah successfully intercepts the OAuth requests and gets the token.
     * @param username
     * @param password
     * @throws IOException
     */
    private void getToken(final String username, String password) throws IOException {
        // Maximum password length is 15 (sign in page enforces this limit, API does not)
        final String trimmedPassword = password.length() > 15 ? password.substring(0, 15) : password;

        final OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })
                .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext())))
                .followRedirects(false)
                .followSslRedirects(false)
                .build();

        Request initialRequest = new Request.Builder()
                .addHeader("User-Agent", "Niantic App")
                .url(LOGIN_URL)
                .build();

        client.newCall(initialRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "fuck :(", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                try {
                    JSONObject data = new JSONObject(body);
                    Log.d(TAG, data.toString());

                    RequestBody formBody = new FormBody.Builder()
                            .add("lt", data.getString("lt"))
                            .add("execution", data.getString("execution"))
                            .add("_eventId", "submit")
                            .add("username", username)
                            .add("password", trimmedPassword)
                            .build();

                    Request interceptRedirect = new Request.Builder()
                            .addHeader("User-Agent", "Niantic App")
                            .url(LOGIN_URL)
                            .post(formBody)
                            .build();

                    client.newCall(interceptRedirect).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "fuck :(", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d(TAG, String.valueOf(response.code())); // should be a 302 (redirect)
                            Log.d(TAG, response.headers().toString()); // should contain a "Location" header

                            if(response.code() != 302 || response.header("Location") == null) {

                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getBaseContext(), getString(R.string.toast_credentials), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );

                                return;
                            }

                            String ticket = response.header("Location").split("ticket=")[1];

                            RequestBody loginForm = new FormBody.Builder()
                                    .add("client_id", CLIENT_ID)
                                    .add("redirect_uri", REDIRECT_URI)
                                    .add("client_secret", PTC_CLIENT_SECRET)
                                    .add("grant_type", "refresh_token")
                                    .add("code", ticket)
                                    .build();

                            Request loginRequest = new Request.Builder()
                                    .addHeader("User-Agent", "Niantic App")
                                    .url(LOGIN_OAUTH)
                                    .post(loginForm)
                                    .build();

                            client.newCall(loginRequest).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String rawToken = response.body().string();
                                    String cleanToken = rawToken.replaceAll("&expires.*", "").replaceAll(".*access_token=", "");

                                    Log.d(TAG, cleanToken); // success!

                                    token = cleanToken;
                                    RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo auth= new PTCLogin(client).login(token);
                                    pokemonGo = new PokemonGo(auth,client);
                                    Log.d("Profile Info: ",pokemonGo.getPlayerProfile().toString());
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Needs to be translated from the Python library.
     * See https://github.com/AHAAAAAAA/PokemonGo-Map
     * See https://github.com/AHAAAAAAA/PokemonGo-Map/blob/master/example.py#L378
     */

    private void getPokemon(){
        Log.d("getPokemon","trying to get catchable Pokemon");
        AsyncTask<Object,Object,Object>asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                /*try {
                    List<Point> spawnPoints = pokemonGo.getMap().getSpawnPoints();
                    mMapWrapperFragment.setSpawnPoints(spawnPoints);
                }catch(Exception e){
                    Log.d("Error spawnPoints:",e.getMessage());
                }*/

                float lat=-SEARCH_RANGE;
                float lon = -SEARCH_RANGE;
                List<CatchablePokemon> pokeList=null;
                Location loc = new Location(mLastLocation);
                while(lat<SEARCH_RANGE) {
                    loc.setLatitude(mLastLocation.getLatitude()+lat);
                    while (lon < SEARCH_RANGE){
                        loc.setLongitude(mLastLocation.getLongitude()+lon);
                        pokemonGo.setLocation(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
                        try {
                            pokeList = pokemonGo.getMap().getCatchablePokemon();
                            Thread.sleep(100);
                        } catch (Exception e) {
                            Log.d("Error pokeFetch:", e.getMessage());
                        }

                        if (pokeList != null) {
                        Log.d("Catchable", "poke on lat " + loc.getLatitude() + " lon:" + loc.getLongitude()+" poke:"+pokeList.size());
                        mMapWrapperFragment.setPokemonMarkers(pokeList);
                        }
                        lon+=GRANULARITY;
                    }
                    lon=-SEARCH_RANGE;
                    lat+=GRANULARITY;
                }
                return null;
            }
        };
        asyncTask.execute();
    }


    private void getHeartBeat() {
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

        Log.d(TAG, "Username: " + username);

        try {
            getToken(username, password);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        pokemonGo.setLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude(),mLastLocation.getAltitude());
        return mLastLocation;
    }

    @Override
    public void onClick(View view) {
       if(view.getId() == R.id.button2) {
           getPokemon();
       }
    }
}
