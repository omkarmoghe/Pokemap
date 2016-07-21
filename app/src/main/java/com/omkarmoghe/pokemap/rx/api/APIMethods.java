package com.omkarmoghe.pokemap.rx.api;

import com.omkarmoghe.pokemap.network.response.InitialResponse;

import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

/**
 * API methods.
 * <p>
 * Created by fess on 20.07.16.
 */
public interface APIMethods {

    @GET("login?service=https://sso.pokemon.com/sso/oauth2.0/callbackAuthorize")
    Observable<Response<InitialResponse>> initialCall(@Header("User-Agent") String userAgent);

    // TODO: 21.07.16 add response classes
    @GET("login?service=https://sso.pokemon.com/sso/oauth2.0/callbackAuthorize")
    Observable<Response<String>> loginCall(@Field("lt") String lt,
                                   @Field("execution") String execution,
                                   @Field("_eventId") String eventId,
                                   @Field("username") String username,
                                   @Field("password") String password,
                                   @Header("User-Agent") String userAgent);

    @GET("oauth2.0/accessToken")
    Observable<Response<String>> tokenCall(@Field("client_id") String clientId,
                                   @Field("redirect_url") String redirectUrl,
                                   @Field("client_secret") String clientSecret,
                                   @Field("grant_type") String grantType,
                                   @Field("code") String ticket,
                                   @Header("User-Agent") String userAgent);
}