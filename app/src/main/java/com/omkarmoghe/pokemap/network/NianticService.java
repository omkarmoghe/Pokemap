package com.omkarmoghe.pokemap.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by vanshilshah on 20/07/16.
 */
public interface NianticService {
    @Headers("User-Agent: Niantic App")
    @POST("/sso/login?service=https://sso.pokemon.com/sso/oauth2.0/callbackAuthorize")
    Call<InitialResponse> login();

    @FormUrlEncoded
    @Headers("User-Agent: Niantic App")
    @POST("/sso/login?service=https://sso.pokemon.com/sso/oauth2.0/callbackAuthorize")
    Call<LoginResponse> completeLogin(@Field("lt") String lt, @Field("execution") String execution, @Field("_eventID") String _eventID, @Field("username") String username, @Field("password") String password);//@Body LoginRequest request);

    class InitialResponse {
        private String lt;
        private String execution;

        public String getLt() {
            return lt;
        }

        public String getExecution() {
            return execution;
        }
    }

    class LoginRequest{
        String lt;
        String execution;
        String _eventId = "submit";
        String username;
        String password;
        public LoginRequest(InitialResponse initialResponse, String username, String password){
            this.lt = initialResponse.lt;
            this.execution = initialResponse.execution;
            this.username = username;
            this.password = password;
        }
    }

    class LoginResponse{

    }
}

