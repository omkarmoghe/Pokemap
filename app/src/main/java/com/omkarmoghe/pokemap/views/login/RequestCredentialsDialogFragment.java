package com.omkarmoghe.pokemap.views.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.omkarmoghe.pokemap.R;

public class RequestCredentialsDialogFragment extends AppCompatDialogFragment {

    private static Listener sListener;

    public static RequestCredentialsDialogFragment newInstance(@Nullable Listener listener) {
        sListener = listener;
        return new RequestCredentialsDialogFragment();
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View rootView = inflater.inflate(R.layout.request_credentials_dialog, null);

        rootView.findViewById(R.id.request_credentials_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (sListener != null) {
                String username = ((EditText) rootView.findViewById(R.id.username)).getText().toString();
                String password = ((EditText) rootView.findViewById(R.id.password)).getText().toString();

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    sListener.credentialsIntroduced(username, password);
                    getDialog().cancel();
                }
            }
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .create();
    }

    public interface Listener {
        void credentialsIntroduced(String username, String password);
    }
}