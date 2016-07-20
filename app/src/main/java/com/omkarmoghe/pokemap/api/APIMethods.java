package com.omkarmoghe.pokemap.api;

import okhttp3.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import rx.Observable;

/**
 * API methods.
 * <p>
 * Created by fess on 20.07.16.
 */
public interface APIMethods {

    @GET(APIUtils.LOGIN_URL)
    Observable<Response> initialCall(@Header("User-Agent") String userAgent);

    @GET(APIUtils.LOGIN_URL)
    Observable<Response> loginCall()

}
