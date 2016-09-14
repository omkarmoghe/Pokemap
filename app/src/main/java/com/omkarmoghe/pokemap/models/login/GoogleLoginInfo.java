package com.omkarmoghe.pokemap.models.login;

/**
 * Created by chris on 7/26/2016.
 */

public class GoogleLoginInfo extends LoginInfo {

    private String mRefreshToken;

    public GoogleLoginInfo(String authToken, String refreshToken){
        super(authToken);
        mRefreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.mRefreshToken = refreshToken;
    }

    @Override
    public String getProvider() {
        return PROVIDER_GOOGLE;
    }
}
