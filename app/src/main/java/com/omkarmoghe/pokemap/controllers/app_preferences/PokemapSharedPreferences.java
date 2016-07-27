package com.omkarmoghe.pokemap.controllers.app_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.ArraySet;
import android.util.Log;

import com.omkarmoghe.pokemap.models.login.GoogleLoginInfo;
import com.omkarmoghe.pokemap.models.login.LoginInfo;
import com.omkarmoghe.pokemap.models.login.PtcLoginInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Provide convenience methods to access shared preferences
 */

public final class PokemapSharedPreferences implements PokemapAppPreferences {
    private static final String TAG = "PokemapSharedPreference";

    private static final String PTC_INFO_KEY = "PTCKey";
    private static final String GOOGLE_INFO_KEY = "GoogleKey";
    private static final String SHOW_SCANNED_PLACES = "scanned_checkbox";
    private static final String SHOW_POKESTOPS = "pokestops_checkbox";
    private static final String SHOW_GYMS = "gyms_checkbox";

    private final SharedPreferences sharedPreferences;

    public PokemapSharedPreferences(@NonNull Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public LoginInfo getLoginInfo() {

        if(sharedPreferences.contains(PTC_INFO_KEY)){
            Set<String> ptcInfo = sharedPreferences.getStringSet(PTC_INFO_KEY, null);
            if(ptcInfo != null && !ptcInfo.isEmpty()) {
                String[] info = ptcInfo.toArray(new String[3]);
                Log.d(TAG, "getLoginInfo: info = " + Arrays.toString(info));
                return new PtcLoginInfo(info[0], info[1], info[2]);
            }
        }

        if(sharedPreferences.contains(GOOGLE_INFO_KEY)) {
            Set<String> googleInfo = sharedPreferences.getStringSet(GOOGLE_INFO_KEY, null);
            if (googleInfo != null) {
                String[] info = googleInfo.toArray(new String[2]);
                Log.d(TAG, "getLoginInfo: info = " + Arrays.toString(info));
                return new GoogleLoginInfo(info[0], info[1]);
            }
        }

        return null;
    }

    @Override
    public void setLoginInfo(LoginInfo loginInfo) {
        Log.d(TAG, "setLoginInfo: LoginInfo = " + loginInfo);

        clearLoginCredentials();

        if(loginInfo instanceof PtcLoginInfo){
            Set<String> info = new HashSet<>();
            PtcLoginInfo ptc = (PtcLoginInfo) loginInfo;
            info.add(ptc.getToken());
            info.add(ptc.getUsername());
            info.add(ptc.getPassword());
            Log.d(TAG, "setLoginInfo: PTCinfo = " + info);
            sharedPreferences.edit().putStringSet(PTC_INFO_KEY, info).apply();
        }

        if(loginInfo instanceof GoogleLoginInfo){
            Set<String> info = new HashSet<>();
            GoogleLoginInfo google = (GoogleLoginInfo) loginInfo;
            info.add(google.getToken());
            info.add(google.getRefreshToken());
            Log.d(TAG, "setLoginInfo: Googleinfo = " + info);
            sharedPreferences.edit().putStringSet(GOOGLE_INFO_KEY, info).apply();
        }
    }

    @Override
    public boolean isLoggedIn() {
        return sharedPreferences.contains(PTC_INFO_KEY) || sharedPreferences.contains(GOOGLE_INFO_KEY);
    }

    @Override
    public boolean getShowScannedPlaces() {
        return sharedPreferences.getBoolean(SHOW_SCANNED_PLACES, false);
    }

    @Override
    public boolean getShowPokestops() {
        return sharedPreferences.getBoolean(SHOW_POKESTOPS, false);
    }

    @Override
    public boolean getShowGyms() {
        return sharedPreferences.getBoolean(SHOW_GYMS, false);
    }

	@Override
    public void clearLoginCredentials() {

        sharedPreferences.edit().remove(GOOGLE_INFO_KEY).apply();
        sharedPreferences.edit().remove(PTC_INFO_KEY).apply();
    }


}
