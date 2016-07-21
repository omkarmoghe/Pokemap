package com.omkarmoghe.pokemap.api;

import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit.Ok3Client;
import com.omkarmoghe.pokemap.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;

/**
 * Network helper class.
 * <p>
 * Created by fess on 20.07.16.
 */
public class NetworkHelper {

    private static RestAdapter.Builder mRetrofitBuilder;

    private static final int TIMEOUT = 1;

    private static NetworkHelper sInstance;

    private NetworkHelper() {

    }

    public static synchronized NetworkHelper getInstance() {
        if (null == sInstance) {
            sInstance = new NetworkHelper();
        }
        return sInstance;
    }

    public <S> S createService(Context context,
                               String apiEndpoint,
                               Class<S> serviceClass) {
        initRestAdapterBuilder(context, apiEndpoint);
        RestAdapter adapter = mRetrofitBuilder.build();
        return adapter.create(serviceClass);
    }

    private RestAdapter.Builder initRestAdapterBuilder(Context context,
                                                       String endpoint) {
        mRetrofitBuilder = createRestAdapter(endpoint);
        mRetrofitBuilder.setClient(new Ok3Client(createHttpClient(context)));
        return mRetrofitBuilder;
    }

    private RestAdapter.Builder createRestAdapter(String endpoint) {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint(endpoint);
        builder.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE);
        return builder;
    }

    private OkHttpClient createHttpClient(Context context) {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.MINUTES)
                .hostnameVerifier((s, sslSession) -> true)
                .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context.getApplicationContext())))
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
    }

    private GsonBuilder createGsonBuilder() {
        return new GsonBuilder();
    }
}
