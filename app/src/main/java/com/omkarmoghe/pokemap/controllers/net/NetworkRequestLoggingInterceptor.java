package com.omkarmoghe.pokemap.controllers.net;

/**
 * Created by vanshilshah on 20/07/16.
 */

import android.util.Log;

import java.io.IOException;
import java.text.MessageFormat;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public  class NetworkRequestLoggingInterceptor implements Interceptor {

    private final String TAG = "NetworkRequest";

    private final String REQUEST_SEND_LOG = "Sending {0} request.\nURL: {1}\nConnection: {2}\nHeaders:\n{3}\n";
    private final String REQUEST_BODY_LOG = "Request body:\n{0}\n";
    private final String RESPONSE_RECEIVE_LOG = "Received response in {0} ms.\nURL: {1}. \nHeaders:\n{2}\n";
    private final String RESPONSE_BODY_LOG = "Response body:\n{0}\n";

    private String sanitize(HttpUrl url) {
        return url.toString().replaceAll("username=[^&]+","username=++++++").replaceAll("password=[^&]+","password=++++++");
    }


    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        final Request request = chain.request();

        // Log request
        Log.d(TAG, MessageFormat.format(REQUEST_SEND_LOG, request.method(), sanitize(request.url()), chain.connection(), request.headers()));
        if(request.method().compareToIgnoreCase("post") == 0)
            Log.d(TAG, MessageFormat.format(REQUEST_BODY_LOG, convertRequestBodyToString(request)));

        final long requestStart = System.currentTimeMillis();
        final Response response = chain.proceed(request);
        final long requestEnd = System.currentTimeMillis();
        final long responseTime = requestEnd - requestStart;

        // Log response
        Log.d(TAG, MessageFormat.format(RESPONSE_RECEIVE_LOG, responseTime, sanitize(response.request().url()), response.headers()));
        final String responseBodyString = response.body().string();
        if(responseBodyString.length() > 0)
            Log.d(TAG, MessageFormat.format(RESPONSE_BODY_LOG, responseBodyString.trim()));

        return response.newBuilder().body(ResponseBody.create(response.body().contentType(), responseBodyString)).build();

    }

    private String convertRequestBodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to convert request body to string via convertRequestBodyToString(). Raised: " + e.getMessage());
            return null;
        }
    }
}