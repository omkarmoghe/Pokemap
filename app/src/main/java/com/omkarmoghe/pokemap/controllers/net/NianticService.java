package com.omkarmoghe.pokemap.controllers.net;

import com.google.gson.annotations.SerializedName;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by vanshilshah on 20/07/16.
 */
public interface NianticService {
    @Headers("User-Agent: Niantic")
    @GET("login?service=https://sso.pokemon.com/sso/oauth2.0/callbackAuthorize")
    Call<LoginValues> getLoginValues();

    @Headers("User-Agent: Niantic")
    @POST
    Call<LoginResponse> login(@Url String url);


    @Headers("User-Agent: Niantic")
    @POST
    Call<ResponseBody> requestToken(@Url String url);

    class LoginValues {
        private String lt;
        private String execution;

        public LoginValues(){
        }

        public String getLt() {
            return lt;
        }
        public String getExecution() {
            return execution;
        }
    }

    class LoginResponse{
        public LoginResponse(){
        }
    }

    class LoginError{
        private String[] errors;
    }
}

