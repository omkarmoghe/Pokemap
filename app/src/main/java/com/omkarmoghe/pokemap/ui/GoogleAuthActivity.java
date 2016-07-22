package com.omkarmoghe.pokemap.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.omkarmoghe.pokemap.R;

public class GoogleAuthActivity extends AppCompatActivity {
    private static final String TAG = "GoogleAuthActivity";

    private static final String ARG_URL = "Google Auth Url";
    private static final String ARG_CODE = "Google User Code";

    private String url;
    private String userCode;

    private WebView webView;
    private TextView codeView;

    public static void startForResult(Activity starter, int requestCode,
                                              String url, String code){
        Intent intent = new Intent(starter, GoogleAuthActivity.class);
        intent.putExtra(ARG_URL, url);
        intent.putExtra(ARG_CODE, code);
        starter.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);

        fetchIntentData();

        codeView = (TextView) findViewById(R.id.auth_code);
        webView = (WebView) findViewById(R.id.webview);

        codeView.setText(userCode);

        WebViewClient client = new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished: url = " + url);
                if(url.contains("https://accounts.google.com/o/oauth2/device/approval?")){
                    sendResults();
                }
            }
        };

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(client);
        webView.loadUrl(url);
    }

    private void fetchIntentData(){
        Intent intent = getIntent();
        url = intent.getStringExtra(ARG_URL);
        userCode = intent.getStringExtra(ARG_CODE);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void sendResults(){
        setResult(RESULT_OK);
        finish();
    }
}
