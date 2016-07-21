package com.omkarmoghe.pokemap.api;

import okhttp3.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Header;
import rx.Observable;

/**
 * API methods.
 * <p>
 * Created by fess on 20.07.16.
 */
public interface APIMethods {

    @GET("/login?service=https://sso.pokemon.com/sso/oauth2.0/callbackAuthorize")
    Observable<Response> initialCall(@Header("User-Agent") String userAgent);

    @GET("/login?service=https://sso.pokemon.com/sso/oauth2.0/callbackAuthorize")
    Observable<Response> loginCall(@Field("lt") String lt,
                                   @Field("execution") String execution,
                                   @Field("_eventId") String eventId,
                                   @Field("username") String username,
                                   @Field("password") String password,
                                   @Header("User-Agent") String userAgent);

    @GET("/oauth2.0/accessToken")
    Observable<Response> tokenCall(@Field("client_id") String clientId,
                                   @Field("redirect_url") String redirectUrl,
                                   @Field("client_secret") String clientSecret,
                                   @Field("grant_type") String grantType,
                                   @Field("code") String ticket,
                                   @Header("User-Agent") String userAgent);

}
