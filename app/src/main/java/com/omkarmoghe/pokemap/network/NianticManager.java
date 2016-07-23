package com.omkarmoghe.pokemap.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private void getPokemon() {
//        try {
//            PokemonOuterClass.RequestEnvelop.Requests.Builder m4 = PokemonOuterClass.RequestEnvelop.Requests.newBuilder();
//            PokemonOuterClass.RequestEnvelop.MessageSingleInt.Builder msi = PokemonOuterClass.RequestEnvelop.MessageSingleInt.newBuilder();
//            msi.setF1(System.currentTimeMillis());
//            m4.setMessage(msi.build().toByteString());
//
//            PokemonOuterClass.RequestEnvelop.Requests.Builder m5 = PokemonOuterClass.RequestEnvelop.Requests.newBuilder();
//            PokemonOuterClass.RequestEnvelop.MessageSingleString.Builder mss = PokemonOuterClass.RequestEnvelop.MessageSingleString.newBuilder();
//            mss.setBytes(ByteString.copyFrom("05daf51635c82611d1aac95c0b051d3ec088a930", "UTF-8"));
//            m5.setMessage(mss.build().toByteString());
//            // TODO: walk = sorted(getNeighbors())
//            PokemonOuterClass.RequestEnvelop.Requests.Builder m1 = PokemonOuterClass.RequestEnvelop.Requests.newBuilder();
//            m1.setType(106); // magic number;
//            PokemonOuterClass.RequestEnvelop.MessageQuad.Builder mq = PokemonOuterClass.RequestEnvelop.MessageQuad.newBuilder();
//            // TODO: mq.f1 = ''.join(map(encode, walk))
//            mq.setF2(ByteString.copyFrom("\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000", "UTF-8"));
//
//            /*requestLocation();
//            mq.setLat(((long) mLastLocation.getLatitude()));
//            mq.setLong(((long) mLastLocation.getLongitude()));
//            */
//            //TODO: connect this to the location provider
//
//            m1.setMessage(mq.build().toByteString());
//
//            // TODO: response = get_profile(...)...
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
