package com.omkarmoghe.pokemap.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapAppPreferences;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;
import com.omkarmoghe.pokemap.controllers.net.GoogleManager;
import com.omkarmoghe.pokemap.controllers.net.GoogleService;
import com.omkarmoghe.pokemap.controllers.net.NianticManager;
import com.omkarmoghe.pokemap.models.login.GoogleLoginInfo;
import com.omkarmoghe.pokemap.models.login.LoginInfo;
import com.omkarmoghe.pokemap.models.login.PtcLoginInfo;

/**
 * A login screen that offers login via username/password. And a Google Sign in
 *
 */
public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";

    private static final int REQUEST_USER_AUTH = 1;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private NianticManager mNianticManager;
    private NianticManager.LoginListener mNianticLoginListener;
    private NianticManager.AuthListener mNianticAuthListener;
    private GoogleManager mGoogleManager;
    private GoogleManager.LoginListener mGoogleLoginListener;

    private PokemapAppPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNianticManager = NianticManager.getInstance();
        mGoogleManager = GoogleManager.getInstance();
        mPref = new PokemapSharedPreferences(this);

        setContentView(R.layout.activity_login);

        mNianticAuthListener = new NianticManager.AuthListener() {
            @Override
            public void authSuccessful() {
                finishLogin();
            }

            @Override
            public void authFailed(String message, String provider) {
                switch (provider){
                    case LoginInfo.PROVIDER_PTC:
                        showPTCLoginFailed();
                        break;
                    case LoginInfo.PROVIDER_GOOGLE:
                        showGoogleLoginFailed();
                        break;
                }
                Log.d(TAG, "authFailed() called with: message = [" + message + "]");
            }
        };

        mNianticLoginListener = new NianticManager.LoginListener() {
            @Override
            public void authSuccessful(String authToken) {
                Log.d(TAG, "authSuccessful() called with: authToken = [" + authToken + "]");
                PtcLoginInfo info = new PtcLoginInfo(authToken,
                        mUsernameView.getText().toString(), mPasswordView.getText().toString());
                mPref.setLoginInfo(info);
                mNianticManager.setLoginInfo(LoginActivity.this, info, mNianticAuthListener);
            }

            @Override
            public void authFailed(String message) {
                Log.e(TAG, "Failed to authenticate. authFailed() called with: message = [" + message + "]");
                showPTCLoginFailed();
            }
        };

        mGoogleLoginListener = new GoogleManager.LoginListener() {
            @Override
            public void authSuccessful(String authToken, String refreshToken) {
                GoogleLoginInfo info = new GoogleLoginInfo(authToken, refreshToken);
                Log.d(TAG, "authSuccessful() called with: authToken = [" + authToken + "]");
                mPref.setLoginInfo(info);
                mNianticManager.setLoginInfo(LoginActivity.this, info, mNianticAuthListener);
            }

            @Override
            public void authFailed(String message) {
                Log.d(TAG, "Failed to authenticate. authFailed() called with: message = [" + message + "]");
                showGoogleLoginFailed();
            }

            @Override
            public void authRequested(GoogleService.AuthRequest body) {
                //Do nothing
            }
        };

        findViewById(R.id.txtDisclaimer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(getString(R.string.login_warning_title))
                        .setMessage(Html.fromHtml(getString(R.string.login_warning)))
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });

        // Set up the triggerAutoLogin form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    validatePTCLoginForm();
                    return true;
                }
                return false;
            }
        });

        LoginInfo loginInfo = mPref.getLoginInfo();
        if(loginInfo != null && loginInfo instanceof PtcLoginInfo){
            mUsernameView.setText(((PtcLoginInfo) loginInfo).getUsername());
            mPasswordView.setText(((PtcLoginInfo) loginInfo).getPassword());
        }

        Button signInButton = (Button) findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePTCLoginForm();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        SignInButton signInButtonGoogle = (SignInButton) findViewById(R.id.sign_in_button);
        signInButtonGoogle.setSize(SignInButton.SIZE_WIDE);
        signInButtonGoogle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleAuthActivity.startForResult(LoginActivity.this, REQUEST_USER_AUTH);
            }
        });

        triggerAutoLogin();
    }

    private void showPTCLoginFailed() {
        showProgress(false);
        Snackbar.make((View)mLoginFormView.getParent(), getString(R.string.toast_ptc_login_error), Snackbar.LENGTH_LONG).show();
    }

    private void showGoogleLoginFailed() {
        showProgress(false);
        Snackbar.make((View)mLoginFormView.getParent(), getString(R.string.toast_google_login_error), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_USER_AUTH){
                showProgress(true);
                mGoogleManager.requestToken(data.getStringExtra(GoogleAuthActivity.EXTRA_CODE),
                        mGoogleLoginListener);
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the triggerAutoLogin form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual triggerAutoLogin attempt is made.
     */
    private void validatePTCLoginForm() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the triggerAutoLogin attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt triggerAutoLogin and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user triggerAutoLogin attempt.
            showProgress(true);
            mNianticManager.login(username, password, mNianticLoginListener);
        }
    }

    private void finishLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Shows the progress UI and hides the triggerAutoLogin form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void triggerAutoLogin() {
        if(mPref.isLoggedIn()){
            showProgress(true);
            mNianticManager.setLoginInfo(this, mPref.getLoginInfo(), mNianticAuthListener);
        }
    }
}

