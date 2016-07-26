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
import com.omkarmoghe.pokemap.models.events.LoginEventResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    private GoogleManager mGoogleManager;
    private GoogleManager.LoginListener mGoogleLoginListener;

    private String mDeviceCode;
    private PokemapAppPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNianticManager = NianticManager.getInstance();
        mGoogleManager = GoogleManager.getInstance();
        mPref = new PokemapSharedPreferences(this);

        setContentView(R.layout.activity_login);

        mNianticLoginListener = new NianticManager.LoginListener() {
            @Override
            public void authSuccessful(String authToken) {
                showProgress(false);
                Log.d(TAG, "authSuccessful() called with: authToken = [" + authToken + "]");
                mNianticManager.setPTCAuthToken(authToken);

                // store prefs
                mPref.setUsername(mUsernameView.getText().toString());
                mPref.setPassword(mPasswordView.getText().toString());

                finishLogin();
            }

            @Override
            public void authFailed(String message) {
                Log.e(TAG, "Failed to authenticate. authFailed() called with: message = [" + message + "]");
                showAuthFailed();
            }
        };

        mGoogleLoginListener = new GoogleManager.LoginListener() {
            @Override
            public void authSuccessful(String authToken) {
                showProgress(false);
                mPref.setGoogleToken(authToken);
                Log.d(TAG, "authSuccessful() called with: authToken = [" + authToken + "]");
                mNianticManager.setGoogleAuthToken(authToken);
                finishLogin();
            }

            @Override
            public void authFailed(String message) {
                showProgress(false);
                Log.e(TAG, "Failed to authenticate. authFailed() called with: message = [" + message + "]");
                Snackbar.make((View)mLoginFormView.getParent(), "Google Login Failed", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void authRequested(GoogleService.AuthRequest body) {
                GoogleAuthActivity.startForResult(LoginActivity.this, REQUEST_USER_AUTH,
                        body.getVerificationUrl(), body.getUserCode());
                mDeviceCode = body.getDeviceCode();
            }
        };

        findViewById(R.id.txtDisclaimer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(getString(R.string.login_warning_title))
                        .setMessage(Html.fromHtml(getString(R.string.login_warning) + "<b>"+getString(R.string.ban)+"</b>"))
                        .setPositiveButton("OK", null)
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
                mGoogleManager.authUser(mGoogleLoginListener);
            }
        });

        triggerAutoLogin();
    }

    private void showAuthFailed() {

        showProgress(false);

        // set Ptc credentials (remembering)
        mUsernameView.setText(mPref.getUsername());
        mPasswordView.setText(mPref.getPassword());

        Snackbar.make((View)mLoginFormView.getParent(),getString(R.string.toast_ptc_login_error), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_USER_AUTH){
                showProgress(true);
                mGoogleManager.requestToken(mDeviceCode, mGoogleLoginListener);
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

        // auto-login is reading from here,
        // if not persisted, you can't change the values in the form
        mPref.setPassword(password);
        mPref.setUsername(username);

        if (cancel) {
            // There was an error; don't attempt triggerAutoLogin and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user triggerAutoLogin attempt.
            triggerAutoLogin();
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

        if (mPref.isUsernameSet() || mPref.isPasswordSet()) {

            showProgress(true);

            mNianticManager.login(mPref.getUsername(), mPref.getPassword());

        } else if (mPref.isGoogleTokenAvailable()) {

            showProgress(true);

            mNianticManager.setGoogleAuthToken(mPref.getGoogleToken());
        }
    }

    /**
     * Called whenever a LoginEventResult is posted to the bus. Originates from LoginTask.java
     *
     * @param result Results of a log in attempt
     */
    @Subscribe
    public void onEvent(final LoginEventResult result) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (result.isLoggedIn()) {

                    finishLogin();

                } else {

                    showAuthFailed();
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}

