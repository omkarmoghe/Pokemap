package com.omkarmoghe.pokemap.controllers.map.directions;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.TypedValue;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapAppPreferences;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;
import com.omkarmoghe.pokemap.models.map.directions.RouteOption;
import com.omkarmoghe.pokemap.models.map.directions.RouteOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by carsten on 28-07-16.
 * Library documentation: http://www.akexorcist.com/2015/12/google-direction-library-for-android-en.html
 */

public class DirectionsManager {

    private Context context;
    // Get Directions Server API Key from prefs
    private PokemapAppPreferences mPref;

    private DirectionsListener directionsListener;

    private Marker selectedMarker;
    private RouteOptions currentRouteOptions;
    public RouteOptions getCurrentRouteOptions() { return currentRouteOptions; }

    private Polyline currentPolyLine;
    public Polyline getCurrentPolyLine() { return currentPolyLine; }

    private static DirectionsManager instance = new DirectionsManager();
    public static DirectionsManager getInstance(Context context) {
        instance.context = context;
        instance.mPref = new PokemapSharedPreferences(context);
        return instance;
    }

    public void register(DirectionsListener directionsListener) {
        this.directionsListener = directionsListener;
    }
    public void unregister(DirectionsListener directionsListener) {
        directionsListener = null;
    }

    public boolean hasServerAPIKey() {
        String key = mPref.getDirectionsAPIKey();
        return !TextUtils.isEmpty(key) && TextUtils.getTrimmedLength(key) > 0;
    }

    public void getRouteOptions(Marker marker, final LatLng from, final LatLng to) {
        selectedMarker = marker;

        // Remove previous directions line because we only want 1
        if (currentPolyLine != null) {
            currentPolyLine.remove();
        }

        if (!hasServerAPIKey()) {
            notifyRouteOptionsFailed(context.getString(R.string.directions_error_key_not_set));
        } else {
            final RouteOptions options = new RouteOptions();

            // Get current theme primary color
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            final int lineColor = typedValue.data;

            final DecimalFormat durationFormat = new DecimalFormat("0.00");

            // GET WALKING INFO
            GoogleDirection.withServerKey(mPref.getDirectionsAPIKey())
                    .from(from)
                    .to(to)
                    .transportMode(TransportMode.WALKING)
                    .unit(Unit.METRIC)
                    .language(Locale.getDefault().getLanguage())
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if (direction.isOK()) {
                                // No alternative routes asked so only 1 Route in list
                                // No waypoints set so only one Leg in list
                                Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();

                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(context, directionPositionList, 5, lineColor);

                                int durationSec = Integer.parseInt(leg.getDuration().getValue());
                                float durationMin = (float) durationSec / 60;

                                options.setWalkOption(new RouteOption(
                                        from,
                                        to,
                                        TransportMode.WALKING,
                                        leg.getDistance().getValue(),
                                        durationFormat.format(durationMin),
                                        polylineOptions));

                                // Only notify is all options are found, otherwise let one of the other options notify
                                if (options.isComplete()) {
                                    notifyRouteOptionsFound(options);
                                }
                            } else {
                                // getErrorMessage() or getStatus() ?
                                notifyRouteOptionsFailed(direction.getErrorMessage());
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            // t.getCause() or t.getLocalizedMessage() or t.getMessage() ?
                            notifyRouteOptionsFailed(t.getLocalizedMessage());
                        }
                    });

            // GET BICYCLING INFO
            GoogleDirection.withServerKey(mPref.getDirectionsAPIKey())
                    .from(from)
                    .to(to)
                    .transportMode(TransportMode.BICYCLING)
                    .unit(Unit.METRIC)
                    .language(Locale.getDefault().getLanguage())
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if (direction.isOK()) {
                                // No alternative routes asked so only 1 Route in list
                                // No waypoints set so only one Leg in list
                                Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();

                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(context, directionPositionList, 5, lineColor);

                                int durationSec = Integer.parseInt(leg.getDuration().getValue());
                                float durationMin = (float) durationSec / 60;

                                options.setBikeOption(new RouteOption(
                                        from,
                                        to,
                                        TransportMode.WALKING,
                                        leg.getDistance().getValue(),
                                        durationFormat.format(durationMin),
                                        polylineOptions));
                                if (options.isComplete()) {
                                    notifyRouteOptionsFound(options);
                                }
                            } else {
                                // getErrorMessage() or getStatus() ?
                                notifyRouteOptionsFailed(direction.getErrorMessage());
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            // t.getCause() or t.getLocalizedMessage() or t.getMessage() ?
                            notifyRouteOptionsFailed(t.getLocalizedMessage());
                        }
                    });

            // GET DRIVING INFO
            GoogleDirection.withServerKey(mPref.getDirectionsAPIKey())
                    .from(from)
                    .to(to)
                    .transportMode(TransportMode.DRIVING)
                    .unit(Unit.METRIC)
                    .language(Locale.getDefault().getLanguage())
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if (direction.isOK()) {
                                // No alternative routes asked so only 1 Route in list
                                // No waypoints set so only one Leg in list
                                Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();

                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(context, directionPositionList, 5, lineColor);

                                int durationSec = Integer.parseInt(leg.getDuration().getValue());
                                float durationMin = (float) durationSec / 60;

                                options.setCarOption(new RouteOption(
                                        from,
                                        to,
                                        TransportMode.WALKING,
                                        leg.getDistance().getValue(),
                                        durationFormat.format(durationMin),
                                        polylineOptions));
                                if (options.isComplete()) {
                                    notifyRouteOptionsFound(options);
                                }
                            } else {
                                // getErrorMessage() or getStatus() ?
                                notifyRouteOptionsFailed(direction.getErrorMessage());
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            // t.getCause() or t.getLocalizedMessage() or t.getMessage() ?
                            notifyRouteOptionsFailed(t.getLocalizedMessage());
                        }
                    });
        }
    }

    public void startDirections(GoogleMap map, RouteOption chosenOption) {
        currentPolyLine = map.addPolyline(chosenOption.getPolyLine());
        selectedMarker.hideInfoWindow();

        // todo: update current route / currentPolyLine...?
    }

    private void notifyRouteOptionsFound(RouteOptions options) {
        if (directionsListener != null) {
            currentRouteOptions = options;
            directionsListener.routeOptionsFound(options);
        }
    }
    private void notifyRouteOptionsFailed(String reason) {
        if (directionsListener != null) {
            directionsListener.routeOptionsFailed(reason);
        }
    }

    public interface DirectionsListener {
        void routeOptionsFound(RouteOptions options);
        void routeOptionsFailed(String reason);
    }

    /*public interface DirectionsHandlerCallback {
        void directionsCreated(PolylineOptions polylineOptions);
        void directionsUpdated();
        void directionsFailed(String reason);
    }*/
}
