package com.omkarmoghe.pokemap.model;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.omkarmoghe.pokemap.api.APIMethods;
import com.omkarmoghe.pokemap.api.APIUtils;
import com.omkarmoghe.pokemap.api.NetworkHelper;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;

import static com.omkarmoghe.pokemap.MainActivity.TAG;

/**
 * Model part of the MVP pattern for {@link com.omkarmoghe.pokemap.MainActivity}.
 * <p>
 * Created by fess on 20.07.16.
 */
public class MainModel {

    public Observable<String> getToken(Context context, String login, String password) {
        Observable<Pair<String, String>> credentialsObs = Observable.just(password)
                .map(s -> password.length() > 15 ? password.substring(0, 15) : password)
                .map(trimmedPassword -> new Pair<>(login, trimmedPassword);

        NetworkHelper networkHelper = NetworkHelper.getInstance();
        APIMethods apiMethods = networkHelper.createService(context, APIUtils.LOGIN_URL, APIMethods.class);

        Observable.zip(apiMethods.initialCall(APIUtils.NIANTIC_APP),
                credentialsObs,
                new Func2<Response, Pair<String,String>, Object>() {
                    @Override
                    public Object call(Response response, Pair<String, String> credentials) {
                        try {
                            String body = response.body().string();

                            JSONObject data = new JSONObject(body);
                            Log.d(TAG, data.toString());

                            // TODO: 7/21/2016 move this to API
                            RequestBody formBody = new FormBody.Builder()
                                    .add("lt", data.getString("lt"))
                                    .add("execution", data.getString("execution"))
                                    .add("_eventId", "submit")
                                    .add("username", credentials.first)
                                    .add("password", credentials.second)
                                    .build();

                            Request interceptRedirect = new Request.Builder()
                                    .addHeader("User-Agent", "Niantic App")
                                    .url(APIUtils.LOGIN_URL)
                                    .post(formBody)
                                    .build();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            return null;
                        }
                    }
                });


                // TODO: 20.07.16 use retrofit observable
        return null;
    }

    private Observable<OkHttpClient> getHttpClient(Context context) {
        return Observable.create(new Observable.OnSubscribe<OkHttpClient>() {
            @Override
            public void call(Subscriber<? super OkHttpClient> subscriber) {
                OkHttpClient client = new OkHttpClient.Builder()
                        .hostnameVerifier((s, sslSession) -> true)
                        .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context.getApplicationContext())))
                        .followRedirects(false)
                        .followSslRedirects(false)
                        .build();
                subscriber.onNext(client);
                subscriber.onCompleted();
            }
        });
    }
}
