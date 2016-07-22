package com.omkarmoghe.pokemap.network;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.gms.maps.model.LatLng;
import com.google.protobuf.ByteString;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.map.LocationManager;
import com.omkarmoghe.pokemap.protobuf.PokemonOuterClass;
import com.omkarmoghe.pokemap.utils.Varint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vanshilshah on 20/07/16.
 */
public class NianticManager {
    private static final String TAG = "NianticManager";
    private static final String BASE_URL = "https://sso.pokemon.com/sso/";
    private static final String LOGIN_URL = "https://sso.pokemon.com/sso/login?service=https://sso.pokemon.com/sso/oauth2.0/callbackAuthorize";
    private static final String LOGIN_OAUTH = "https://sso.pokemon.com/sso/oauth2.0/accessToken";
    private static final String PTC_CLIENT_SECRET = "w8ScCUXJQc6kXKw8FiOhd8Fixzht18Dq3PEVkUCP5ZPxtgyWsbTvWHFLm2wNY0JR";
    public static final String CLIENT_ID = "mobile-app_pokemon-go";
    public static final String REDIRECT_URI = "https://www.nianticlabs.com/pokemongo/error";

    private static NianticManager instance;

    private List<Listener> listeners;
    private Context context;

    NianticService nianticService;
    final OkHttpClient client;

    public static NianticManager getInstance(Context context){
        if (instance == null){
            instance = new NianticManager();
            instance.context = context;
        }
        return instance;
    }

    private NianticManager() {
        listeners = new ArrayList<>();

        client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })
                .addInterceptor(new LoggingInterceptor())
                .followRedirects(false)
                .followSslRedirects(false)
                .build();

        nianticService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(NianticService.class);
    }

    public void login(final String username, final String password, Context context) {
        //retrofitLogin(username, password);
        try {
            traditionalLogin(username, password, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void retrofitLogin(final String username, final String password) {
        Callback<NianticService.InitialResponse> initialCallback = new Callback<NianticService.InitialResponse>() {
            @Override
            public void onResponse(Call<NianticService.InitialResponse> call, Response<NianticService.InitialResponse> response) {
                retrofitLoginStep2(new NianticService.LoginRequest(response.body(), username, password));
            }

            @Override
            public void onFailure(Call<NianticService.InitialResponse> call, Throwable t) {

            }
        };
        Call<NianticService.InitialResponse> call = nianticService.login();
        call.enqueue(initialCallback);
    }

    private void retrofitLoginStep2(NianticService.LoginRequest loginRequest) {
        Callback<NianticService.LoginResponse> loginCallback = new Callback<NianticService.LoginResponse>() {
            @Override
            public void onResponse(Call<NianticService.LoginResponse> call, Response<NianticService.LoginResponse> response) {
                //TODO: make the next call to finish getting the token.
            }

            @Override
            public void onFailure(Call<NianticService.LoginResponse> call, Throwable t) {

            }
        };
        Call<NianticService.LoginResponse> call = nianticService.completeLogin(loginRequest.lt, loginRequest.execution, loginRequest._eventId, loginRequest.username, loginRequest.password);
        call.enqueue(loginCallback);
    }

    private void traditionalLogin(final String username, String password, final Context context) throws IOException {
        // Maximum password length is 15 (sign in page enforces this limit, API does not)
        final String trimmedPassword = password.length() > 15 ? password.substring(0, 15) : password;

        final OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })
                .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context)))
                .addInterceptor(new LoggingInterceptor())
                .followRedirects(false)
                .followSslRedirects(false)
                .build();

        Request initialRequest = new Request.Builder()
                .addHeader("User-Agent", "Niantic App")
                .url(LOGIN_URL)
                .build();

        client.newCall(initialRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(TAG, "fuck :(", e);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
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

                    client.newCall(interceptRedirect).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.e(TAG, "fuck :(", e);
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            Log.d(TAG, String.valueOf(response.code())); // should be a 302 (redirect)
                            Log.d(TAG, response.headers().toString()); // should contain a "Location" header

                            if (response.code() != 302 || response.header("Location") == null) {
                                if (context instanceof Activity) {
                                    ((Activity) context).runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, context.getString(R.string.toast_credentials), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                    );
                                }

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

                            client.newCall(loginRequest).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(okhttp3.Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                    String rawToken = response.body().string();
                                    String cleanToken = rawToken.replaceAll("&expires.*", "").replaceAll(".*access_token=", "");

                                    Log.d(TAG, cleanToken); // success!

                                    //token = cleanToken;
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
    private void getPokemon(Location location) {
        try {
            PokemonOuterClass.RequestEnvelop.Requests.Builder m4 = PokemonOuterClass.RequestEnvelop.Requests.newBuilder();
            PokemonOuterClass.RequestEnvelop.MessageSingleInt.Builder msi = PokemonOuterClass.RequestEnvelop.MessageSingleInt.newBuilder();
            msi.setF1(System.currentTimeMillis());
            m4.setMessage(msi.build().toByteString());

            PokemonOuterClass.RequestEnvelop.Requests.Builder m5 = PokemonOuterClass.RequestEnvelop.Requests.newBuilder();
            PokemonOuterClass.RequestEnvelop.MessageSingleString.Builder mss = PokemonOuterClass.RequestEnvelop.MessageSingleString.newBuilder();
            mss.setBytes(ByteString.copyFrom("05daf51635c82611d1aac95c0b051d3ec088a930", "UTF-8"));
            m5.setMessage(mss.build().toByteString());
            // TODO: walk = sorted(getNeighbors())
            // TODO: Check if this is right
            ArrayList<Integer> walk = getNeighbors();
            PokemonOuterClass.RequestEnvelop.Requests.Builder m1 = PokemonOuterClass.RequestEnvelop.Requests.newBuilder();
            m1.setType(106); // magic number;
            PokemonOuterClass.RequestEnvelop.MessageQuad.Builder mq = PokemonOuterClass.RequestEnvelop.MessageQuad.newBuilder();
            // TODO: mq.f1 = ''.join(map(encode, walk))
            // TODO: Check if this is right
            mq.setF1(encode(walk));
            mq.setF2(ByteString.copyFrom("\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000", "UTF-8"));

            LatLng latLng = LocationManager.getInstance(context).getLocation();
            mq.setLat((long) latLng.latitude);
            mq.setLong((long) latLng.longitude);

            //TODO: connect this to the location provider

            m1.setMessage(mq.build().toByteString());

            // TODO: response = get_profile(...)...
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: Find right place for this
    private ArrayList<Integer> getNeighbors() {

        Integer origin = null;
        final TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
            if (location != null) {
                origin = location.getCid();
            }
        }

        if (origin == null) {
            return null;
        }

        ArrayList<Integer> walk = new ArrayList<>();
        walk.add(origin);

        for (int i = 1; i <= 10; i++) {
            Integer next = origin + i;
            walk.add(next);
        }

        Collections.sort(walk);

        return walk;
    }

    //TODO: Find right place for this
    private ByteString encode(ArrayList<Integer> walk) {
        if (walk == null) {
            return null;
        }

        byte[] mainBytes = null;

        for (Integer cellid: walk) {

            byte[] bytes = Varint.writeUnsignedVarInt(cellid);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try {
                if (mainBytes != null) {
                    outputStream.write(mainBytes);
                }
                outputStream.write( bytes );
            } catch (IOException e) {
                e.printStackTrace();
            }

            mainBytes = outputStream.toByteArray( );

        }

        assert mainBytes != null;
        return ByteString.copyFrom(mainBytes);
    }

    public interface Listener{

    }

}
