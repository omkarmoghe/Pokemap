package com.omkarmoghe.pokemap.controllers.map.directions;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.akexorcist.googledirection.constant.TransportMode;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.omkarmoghe.pokemap.R;

/**
 * Created by carsten on 28-07-16.
 */

public class InformationWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity activity;

    public InformationWindowAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file info_window_layout
        View v = activity.getLayoutInflater().inflate(R.layout.marker_info_window, null);

        // Getting reference to the TextView to set latitude
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) v.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());

        // Returning the view containing InfoWindow contents
        return v;
    }
}