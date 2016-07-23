package com.omkarmoghe.pokemap.network;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omkarmoghe.pokemap.map.LocationManager;
import com.omkarmoghe.pokemap.protobuf.PokemonOuterClass;
import com.omkarmoghe.pokemap.utils.Varint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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
    private static final String LOGIN_URL = "https://sso.pokemon.com/sso/login?service=https" +
            "%3A%2F%2Fsso.pokemon.com%2Fsso%2Foauth2.0%2FcallbackAuthorize";
    private static final String LOGIN_OAUTH = "https://sso.pokemon.com/sso/oauth2.0/accessToken";

    private static final String CLIENT_SECRET = "w8ScCUXJQc6kXKw8FiOhd8Fixzht18Dq3PEVkUCP5ZP" +
            "xtgyWsbTvWHFLm2wNY0JR";
    private static final String CLIENT_ID = "mobile-app_pokemon-go";
    private static final String REDIRECT_URI = "https://www.nianticlabs.com/pokemongo/error";

    private static NianticManager instance;

    private NianticService mNianticService;
    private final OkHttpClient mClient;
    private List<Listener> listeners;

    public static NianticManager getInstance(){
        if(instance == null){
            instance = new NianticManager();
        }
        return instance;
    }

    private NianticManager(){
          /*
		This is a temporary, in-memory cookie jar.
		We don't require any persistence outside of the scope of the login,
		so it being discarded is completely fine
		*/
        CookieJar tempJar = new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

            @Override
            public void saveFromResponse(okhttp3.HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(okhttp3.HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        mClient = new OkHttpClient.Builder()
                .cookieJar(tempJar)
                .addInterceptor(new LoggingInterceptor())
                .build();

        mNianticService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(mClient)
                .build()
                .create(NianticService.class);
    }

    public void login(final String username, final String password, final CallBack callBack){
        Callback<NianticService.LoginValues> valuesCallback = new Callback<NianticService.LoginValues>() {
            @Override
            public void onResponse(Call<NianticService.LoginValues> call, Response<NianticService.LoginValues> response) {
                if(response.body() != null) {
                    loginPTC(username, password, response.body(), callBack);
                }else{
                    callBack.authFailed("Fetching Pokemon Trainer Club's Login Url Values Failed");
                }

            }

            @Override
            public void onFailure(Call<NianticService.LoginValues> call, Throwable t) {
                callBack.authFailed("Fetching Pokemon Trainer Club's Login Url Values Failed");
            }
        };
        Call<NianticService.LoginValues> call = mNianticService.getLoginValues();
        call.enqueue(valuesCallback);
    }

    private void loginPTC(final String username, final String password, NianticService.LoginValues values, final CallBack callBack){
        HttpUrl url = HttpUrl.parse(LOGIN_URL).newBuilder()
                .addQueryParameter("lt", values.getLt())
                .addQueryParameter("execution", values.getExecution())
                .addQueryParameter("_eventId", "submit")
                .addQueryParameter("username", username)
                .addQueryParameter("password", password)
                .build();

        OkHttpClient client = mClient.newBuilder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
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

        NianticService service = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(NianticService.class);

        Callback<NianticService.LoginResponse> loginCallback = new Callback<NianticService.LoginResponse>() {
            @Override
            public void onResponse(Call<NianticService.LoginResponse> call, Response<NianticService.LoginResponse> response) {
                String location = response.headers().get("location");
                String ticket = location.split("ticket=")[1];
                requestToken(ticket, callBack);
            }

            @Override
            public void onFailure(Call<NianticService.LoginResponse> call, Throwable t) {
                callBack.authFailed("Pokemon Trainer Club Login Failed");
            }
        };
        Call<NianticService.LoginResponse> call = service.login(url.toString());
        call.enqueue(loginCallback);
    }

    private void requestToken(String code, final CallBack callBack){
        Log.d(TAG, "requestToken() called with: code = [" + code + "]");
        HttpUrl url = HttpUrl.parse(LOGIN_OAUTH).newBuilder()
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("redirect_uri", REDIRECT_URI)
                .addQueryParameter("client_secret", CLIENT_SECRET)
                .addQueryParameter("grant_type", "refresh_token")
                .addQueryParameter("code", code)
                .build();

        Callback<ResponseBody> authCallback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String token = response.body().string().split("token=")[1];
                    token = token.split("&")[0];
                    callBack.authSuccessful(token);
                } catch (IOException e) {
                    e.printStackTrace();
                    callBack.authFailed("Pokemon Trainer Club Authentication Failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                callBack.authFailed("Pokemon Trainer Club Authentication Failed");
            }
        };
        Call<ResponseBody> call = mNianticService.requestToken(url.toString());
        call.enqueue(authCallback);
    }

    public interface CallBack {
        void authSuccessful(String authToken);
        void authFailed(String message);
    }

    /**
     * Needs to be translated from the Python library.
     * See https://github.com/AHAAAAAAA/PokemonGo-Map
     * See https://github.com/AHAAAAAAA/PokemonGo-Map/blob/master/example.py#L378
     */
    private void getPokemon(Context context) {
        try {
            PokemonOuterClass.RequestEnvelop.Requests m4 = new PokemonOuterClass.RequestEnvelop.Requests();
            PokemonOuterClass.RequestEnvelop.MessageSingleInt msi = new PokemonOuterClass.RequestEnvelop.MessageSingleInt();
            msi.f1 = System.currentTimeMillis();
            m4.message = msi.toString().getBytes();

            PokemonOuterClass.RequestEnvelop.Requests m5 = new PokemonOuterClass.RequestEnvelop.Requests();
            PokemonOuterClass.RequestEnvelop.MessageSingleString mss = new PokemonOuterClass.RequestEnvelop.MessageSingleString();
            mss.bytes = "05daf51635c82611d1aac95c0b051d3ec088a930".getBytes("UTF-8");
            m5.message = mss.toString().getBytes();
            // TODO: walk = sorted(getNeighbors())
            // TODO: Check if this is right
            ArrayList<Integer> walk = getNeighbors(context);
            PokemonOuterClass.RequestEnvelop.Requests m1 = new PokemonOuterClass.RequestEnvelop.Requests();
            m1.type = 106; // magic number
            PokemonOuterClass.RequestEnvelop.MessageQuad mq = new PokemonOuterClass.RequestEnvelop.MessageQuad();
            // TODO: mq.f1 = ''.join(map(encode, walk))
            // TODO: Check if this is right
            mq.f1 = encode(walk);
            mq.f2 = "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000".getBytes("UTF-8");

            LatLng latLng = LocationManager.getInstance(context).getLocation();
            mq.lat = (long) latLng.latitude;
            mq.long_ = (long) latLng.latitude;

            //TODO: connect this to the location provider

            m1.message = mq.toString().getBytes();

            // TODO: response = get_profile(...)...
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: Find right place for this
    private ArrayList<Integer> getNeighbors(Context context) {

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
            Integer prev = origin - i;
            walk.add(next);
            walk.add(prev);
        }

        Collections.sort(walk);

        return walk;
    }

    //TODO: Find right place for this
    private byte[] encode(ArrayList<Integer> walk) {
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
        return mainBytes;
    }

    public interface Listener{

    }

}
