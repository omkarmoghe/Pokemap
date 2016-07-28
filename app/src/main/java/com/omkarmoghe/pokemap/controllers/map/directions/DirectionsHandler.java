package com.omkarmoghe.pokemap.controllers.map.directions;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.views.settings.DirectionsActivity;

import java.util.ArrayList;

public class DirectionsHandler {

    private SharedPreferences sharedPref;

    private Context context;
    private Polyline currentDirectionsPolyLine;
    public void setCurrentDirectionsPolyLine(Polyline polyLine) {
        currentDirectionsPolyLine = polyLine;
    }

    private static DirectionsHandler instance = new DirectionsHandler();
    public static DirectionsHandler getInstance(Context context) {
        instance.context = context;
        instance.sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        return instance;
    }

    public void getDirections(LatLng from, LatLng to, String transportMode, final DirectionsHandlerCallback directionsCallback) {
        // Remove previous directions line because we only want 1
        if (currentDirectionsPolyLine != null) {
            currentDirectionsPolyLine.remove();
        }

        String serverAPIKey = sharedPref.getString(DirectionsActivity.PREF_KEY, "");
        if (TextUtils.isEmpty(serverAPIKey)) {
            directionsCallback.directionsFailed("Directions Server API Key not set");
        } else {
            GoogleDirection.withServerKey(serverAPIKey)
                    .from(from)
                    .to(to)
                    .transportMode(transportMode)
                    .unit(Unit.METRIC)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(final Direction direction, final String rawBody) {

                            if (direction.isOK()) {

                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);

                                    /* Get LatLng start & end of every step */
                                // List<Step> legList = leg.getStepList();
                                // LatLng start = step.getStartLocation().getCoordination();
                                // LatLng end = step.getEndLocation().getCoordination();

                                    /* Get every LatLng directly */
                                // ArrayList<LatLng> pointList = leg.getDirectionPoint();

                                    /* Get route info (also available for step) */
                                // Info distanceInfo = leg.getDistance();
                                // Info durationInfo = leg.getDuration();
                                // String distance = distanceInfo.getText();
                                // String duration = durationInfo.getText();

                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                // todo: color based on theme
                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(context, directionPositionList, 5, ContextCompat.getColor(context, R.color.colorPrimary));
                                //currentDirectionsPolyLine = map.addPolyline(polylineOptions);

                                directionsCallback.directionsCreated(polylineOptions);

                            } else {
                                // getErrorMessage() or getStatus() ?
                                directionsCallback.directionsFailed(direction.getErrorMessage());
                            }
                        }

                        @Override
                        public void onDirectionFailure(final Throwable t) {
                            //t.getCause();
                            //t.getLocalizedMessage();
                            //t.getMessage();
                            directionsCallback.directionsFailed(t.getLocalizedMessage());
                        }
                    });
        }
    }

    public interface DirectionsHandlerCallback {
        void directionsCreated(PolylineOptions polylineOptions);
        void directionsUpdated();
        void directionsFailed(String reason);
    }
}
