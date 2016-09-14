package com.omkarmoghe.pokemap.controllers.net;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chris on 7/21/2016.
 */
public class GoogleManager {
    private static final String TAG = "GoogleManager";

    private static GoogleManager ourInstance = new GoogleManager();

    private static final String BASE_URL = "https://www.google.com";
    private static final String SECRET = "NCjF1TLi2CcY6t5mt0ZveuL7";
    public static final String CLIENT_ID = "848232511240-73ri3t7plvk96pj4f85uj8otdat2alem.apps.googleusercontent.com";
    private static final String OAUTH_TOKEN_ENDPOINT = "https://www.googleapis.com/oauth2/v4/token";
    public static final String OAUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
//    private static final String OAUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/device/code";

    private final OkHttpClient mClient;
    private final GoogleService mGoogleService;

    public static GoogleManager getInstance() {
        return ourInstance;
    }

    private GoogleManager() {
        mClient = new OkHttpClient.Builder()
                .addInterceptor(new NetworkRequestLoggingInterceptor())
                .followRedirects(false)
                .followSslRedirects(false)
                .build();

        mGoogleService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mClient)
                .build()
                .create(GoogleService.class);
    }

    @Deprecated
    @SuppressWarnings({"deprecation","unused"})
    public void authUser(final LoginListener listener) {
        HttpUrl url = HttpUrl.parse(OAUTH_ENDPOINT).newBuilder()
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("scope", "openid email https://www.googleapis.com/auth/userinfo.email")
                .addQueryParameter("response_type","code")
                .addQueryParameter("redirect_uri","http://127.0.0.1:9004")
                .build();

        Callback<GoogleService.AuthRequest> googleCallback = new Callback<GoogleService.AuthRequest>() {
            @Override
            public void onResponse(Call<GoogleService.AuthRequest> call, Response<GoogleService.AuthRequest> response) {
                GoogleService.AuthRequest body = response.body();

                if (body != null) {
                    listener.authRequested(body);
                } else {
                    Log.e(TAG, "Google login failed while authenticating. response.body() is null.");
                    listener.authFailed("Google login failed while authenticating");
                }
            }

            @Override
            public void onFailure(Call<GoogleService.AuthRequest> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "Google authentication failed when calling  authUser(). googleCallback's onFailure() threw: " + t.getMessage());
                listener.authFailed("Failed on getting the information for the user auth");
            }
        };

        if (mGoogleService != null) {
            Call<GoogleService.AuthRequest> call = mGoogleService.requestAuth(url.toString());
            call.enqueue(googleCallback);
        }
    }

    public void requestToken(String deviceCode, final LoginListener listener){
        HttpUrl url = HttpUrl.parse(OAUTH_TOKEN_ENDPOINT).newBuilder()
//                .addQueryParameter("client_id", CLIENT_ID)
//                .addQueryParameter("client_secret", SECRET)
//                .addQueryParameter("code", deviceCode)
//                .addQueryParameter("grant_type", "http://oauth.net/grant_type/device/1.0")
//                .addQueryParameter("scope", "openid email https://www.googleapis.com/auth/userinfo.email")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("code", deviceCode)
                .add("client_id", CLIENT_ID)
                .add("client_secret", SECRET)
                .add("redirect_uri", "http://127.0.0.1:8080")
                .add("grant_type", "authorization_code")
                .build();

        Callback<GoogleService.TokenResponse> googleCallback = new Callback<GoogleService.TokenResponse>() {
            @Override
            public void onResponse(Call<GoogleService.TokenResponse> call, Response<GoogleService.TokenResponse> response) {

                if (response.body() != null) {
                    listener.authSuccessful(response.body().getIdToken(), response.body().getRefreshToken());
                } else {
                    Log.e(TAG, "Google login failed while fetching token. response.body() is null.");
                    listener.authFailed("Google login failed while authenticating. Token missing.");
                }
            }

            @Override
            public void onFailure(Call<GoogleService.TokenResponse> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "Google authentication failed while fetching request token using requestToken(). googleCallback's onFailure() threw: " + t.getMessage());
                listener.authFailed("Failed on requesting the id token");
            }
        };

        if (mGoogleService != null) {
            Call<GoogleService.TokenResponse> call = mGoogleService.requestToken(url.toString(), body);
            call.enqueue(googleCallback);
        }
    }

    public void refreshToken(String refreshToken, final RefreshListener listener) {
        HttpUrl url = HttpUrl.parse(OAUTH_TOKEN_ENDPOINT).newBuilder()
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("client_secret", SECRET)
                .addQueryParameter("refresh_token", refreshToken)
                .addQueryParameter("grant_type", "refresh_token")
                .build();


        Callback<GoogleService.TokenResponse> googleCallback = new Callback<GoogleService.TokenResponse>() {
            @Override
            public void onResponse(Call<GoogleService.TokenResponse> call, Response<GoogleService.TokenResponse> response) {
                if(response != null && response.body() != null) {
                    listener.refreshSuccessful(response.body().getIdToken(), response.body().getRefreshToken());
                }else {
                    listener.refreshFailed("Failed on requesting the id token");
                }
            }

            @Override
            public void onFailure(Call<GoogleService.TokenResponse> call, Throwable t) {
                t.printStackTrace();
                listener.refreshFailed("Failed on requesting the id token");
            }
        };

        if (mGoogleService != null) {
            Call<GoogleService.TokenResponse> call = mGoogleService.requestToken(url.toString());
            call.enqueue(googleCallback);
        }
    }

    public interface LoginListener {
        void authSuccessful(String authToken, String refreshToken);
        void authFailed(String message);
        @Deprecated
        void authRequested(GoogleService.AuthRequest body);
    }

    public interface RefreshListener {
        void refreshSuccessful(String authToken, String refreshToken);
        void refreshFailed(String message);
    }
}
