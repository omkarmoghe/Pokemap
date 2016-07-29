package com.omkarmoghe.pokemap.controllers.net;

import com.google.gson.annotations.SerializedName;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by chris on 7/21/2016.
 */

public interface GoogleService {
    @Deprecated
    @POST
    Call<AuthRequest> requestAuth(@Url String url);

    @POST
    Call<TokenResponse> requestToken(@Url String url, @Body RequestBody body);

    @POST
    Call<TokenResponse> requestToken(@Url String url);

    @Deprecated
    class AuthRequest{
        @SerializedName("device_code")
        String deviceCode;
        @SerializedName("user_code")
        String userCode;
        @SerializedName("verification_url")
        String verificationUrl;
        @SerializedName("expires_in")
        int expiresIn;
        @SerializedName("interval")
        int interval;

        public AuthRequest(){
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public String getUserCode() {
            return userCode;
        }

        public String getVerificationUrl() {
            return verificationUrl;
        }

        public int getExpiresIn() {
            return expiresIn;
        }

        public int getInterval() {
            return interval;
        }
    }

    class TokenResponse {
        @SerializedName("access_token")
        private String accessToken;
        @SerializedName("token_type")
        private String tokenType;
        @SerializedName("expires_in")
        private int expiresIn;
        @SerializedName("refresh_token")
        private String refreshToken;
        @SerializedName("id_token")
        private String idToken;

        public TokenResponse() {
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public int getExpiresIn() {
            return expiresIn;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public String getIdToken() {
            return idToken;
        }
    }
}
