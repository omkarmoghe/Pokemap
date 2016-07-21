package com.omkarmoghe.pokemap.rx.model;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.omkarmoghe.pokemap.app.App;
import com.omkarmoghe.pokemap.rx.api.APIMethods;
import com.omkarmoghe.pokemap.rx.api.APIUtils;
import com.omkarmoghe.pokemap.rx.api.NetworkHelper;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * Model part of the MVP pattern for {@link com.omkarmoghe.pokemap.MainActivity}.
 * <p>
 * Created by fess on 20.07.16.
 */
public class MainModel {

    private static final String TAG = MainModel.class.getSimpleName();

    public MainModel(Application application) {
        App.getMainComponent().inject(this);
    }

    /**
     * @param context  the current context
     * @param login    user login
     * @param password user password
     * @return an observable which completes the three-step handshake and just returns the token.
     */
    public Observable<String> getTokenObs(Context context, String login, String password) {
        String trimmedPassword = password.length() > 15 ? password.substring(0, 15) : password;

        NetworkHelper networkHelper = NetworkHelper.getInstance();
        APIMethods apiMethods = networkHelper.createService(context, APIUtils.LOGIN_URL, APIMethods.class);

        return apiMethods.initialCall(APIUtils.NIANTIC_APP)
                .lift(new Observable.Operator<HashMap<String, String>, Response>() {
                    @Override
                    public Subscriber<? super Response> call(Subscriber<? super HashMap<String, String>> subscriber) {
                        return new Subscriber<Response>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(Response response) {
                                if ((null == response) || (null == response.body())) {
                                    subscriber.onError(new NullPointerException("Response is null"));
                                    return;
                                }
                                try {
                                    String body = response.body().toString();
                                    JSONObject data = new JSONObject(body);
                                    Log.d(TAG, data.toString());

                                    String lt = data.getString("lt");
                                    String execution = data.getString("execution");

                                    HashMap<String, String> dataMap = new HashMap<>();
                                    dataMap.put("lt", lt);
                                    dataMap.put("execution", execution);

                                    subscriber.onNext(dataMap);
                                    subscriber.onCompleted();
                                } catch (Exception e) {
                                    subscriber.onError(e);
                                }
                            }
                        };
                    }
                })
                .flatMap(dataMap -> apiMethods.loginCall(dataMap.get("lt"), dataMap.get("execution"), "submit",
                        login, trimmedPassword, APIUtils.NIANTIC_APP))
                .lift(new Observable.Operator<String, Response>() {
                    @Override
                    public Subscriber<? super Response> call(Subscriber<? super String> subscriber) {
                        return new Subscriber<Response>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(Response response) {
                                Log.d(TAG, String.valueOf(response.code())); // should be a 302 (redirect)
                                Log.d(TAG, response.headers().toString()); // should contain a "Location" header

                                if (response.code() != 302 || response.headers().get("Location") == null) {
                                    subscriber.onError(new IllegalStateException("Response code is not 302 or Location header is null"));
                                    return;
                                }

                                String ticket = response.headers().get("Location").split("ticket=")[1];
                                subscriber.onNext(ticket);
                                subscriber.onCompleted();
                            }
                        };
                    }
                })
                .flatMap(ticket -> apiMethods.tokenCall(APIUtils.CLIENT_ID, APIUtils.REDIRECT_URI,
                        APIUtils.PTC_CLIENT_SECRET, "refresh_token", ticket, APIUtils.NIANTIC_APP))
                .map(response -> {
                    String rawToken = response.body().toString();
                    String cleanToken = rawToken.replaceAll("&expires.*", "").replaceAll(".*access_token=", "");

                    Log.d(TAG, cleanToken); // success!
                    return cleanToken;
                });
    }
}
