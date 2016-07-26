package com.omkarmoghe.pokemap.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.omkarmoghe.pokemap.R;

/**
 * Created by Ilya Gazman on 7/24/2016.
 */
public class PermissionActivity extends AppCompatActivity {

    private static final String MUST_PERMISSIONS[] = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int REQUEST_CODE = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isGranted(MUST_PERMISSIONS)) {
            continueToLoginScreen();
        } else {
            setContentView(R.layout.permissions_layout);
            init();
        }
    }

    private void continueToLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void init() {
        initAllowButton();
        initSettings();
    }

    private void initSettings() {
        findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }

    private void initAllowButton() {
        findViewById(R.id.allowButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission(MUST_PERMISSIONS);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE && isGranted(MUST_PERMISSIONS)) {
            continueToLoginScreen();
        }
    }

    private void askPermission(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
    }

    private boolean isGranted(String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
