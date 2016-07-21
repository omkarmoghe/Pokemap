package com.omkarmoghe.pokemap.rx.api;

import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Network helper class.
 * <p>
 * Created by fess on 20.07.16.
 */
public class NetworkHelper {

    private static Retrofit.Builder mRetrofitBuilder;

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
        if (null == mRetrofitBuilder) {
            mRetrofitBuilder = initRestAdapterBuilder(context, apiEndpoint);
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();
            mRetrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson));
        }

        Retrofit retrofit = mRetrofitBuilder.build();
        return retrofit.create(serviceClass);
    }

    private Retrofit.Builder initRestAdapterBuilder(Context context,
                                                    String endpoint) {
        return new Retrofit.Builder()
                .baseUrl(endpoint)
                .client(createHttpClient(context))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
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
}