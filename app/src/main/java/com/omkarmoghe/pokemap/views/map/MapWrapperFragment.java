package com.omkarmoghe.pokemap.views.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.controllers.map.LocationManager;
import com.omkarmoghe.pokemap.models.map.PokemonMarkerExtended;
import com.omkarmoghe.pokemap.models.events.CatchablePokemonEvent;
import com.omkarmoghe.pokemap.models.events.SearchInPosition;
import com.omkarmoghe.pokemap.views.MainActivity;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 *
 * Use the {@link MapWrapperFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapWrapperFragment extends Fragment implements OnMapReadyCallback,
                                                            GoogleMap.OnMapLongClickListener,
                                                            ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 19;

    private LocationManager locationManager;

    private View mView;
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;
    private Location mLocation = null;
    private Marker userSelectedPositionMarker = null;
    private Circle userSelectedPositionCircle = null;
    private HashMap<String, PokemonMarkerExtended> markerList = new HashMap<>();

    public MapWrapperFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapWrapperFragment.
     */
    public static MapWrapperFragment newInstance() {
        MapWrapperFragment fragment = new MapWrapperFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePokemonMarkers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locationManager = LocationManager.getInstance(getContext());
        locationManager.register(new LocationManager.Listener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mLocation == null) {
                    mLocation = location;
                    initMap();
                }
                else{
                    mLocation = location;
                }
            }
        });
        // Inflate the layout for this fragment if the view is not null
        if (mView == null) mView = inflater.inflate(R.layout.fragment_map_wrapper, container, false);
        else {

        }

        // build the map
        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mSupportMapFragment).commit();
            mSupportMapFragment.setRetainInstance(true);
        }

        if (mGoogleMap == null) {
            mSupportMapFragment.getMapAsync(this);
        }

        FloatingActionButton locationFab = (FloatingActionButton) mView.findViewById(R.id.location_fab);
        locationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLocation != null && mGoogleMap != null) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15));

                    MainActivity.toast.setText("Found you!");
                    MainActivity.toast.show();
                }
                else{

                    MainActivity.toast.setText("Waiting on location...");
                    MainActivity.toast.show();
                }
            }
        });

        mView.findViewById(R.id.closeSuggestions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.findViewById(R.id.layoutSuggestions).setVisibility(View.GONE);
            }
        });

        return mView;
    }
    private void initMap(){
        if (mLocation != null && mGoogleMap != null){
            if (ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                MainActivity.toast.setText("Location permission not granted!");
                MainActivity.toast.show();
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15));
            MainActivity.toast.setText("Found you!");
            MainActivity.toast.show();
        }
    }

    private void updatePokemonMarkers() {
        if (mGoogleMap != null && markerList != null && !markerList.isEmpty()){
            for(Iterator<Map.Entry<String, PokemonMarkerExtended>> it = markerList.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, PokemonMarkerExtended> entry = it.next();
                CatchablePokemon catchablePokemon = entry.getValue().getCatchablePokemon();
                Marker marker = entry.getValue().getMarker();
                long millisLeft = catchablePokemon.getExpirationTimestampMs() - System.currentTimeMillis();
                if(millisLeft < 0) {
                    marker.remove();
                    it.remove();
                } else {
                    marker.setSnippet(getExpirationBreakdown(millisLeft));
                    if(marker.isInfoWindowShown()) {
                        marker.showInfoWindow();
                    }
                }
            }

        }
    }

    private void setPokemonMarkers(final List<CatchablePokemon> pokeList){
        if (mGoogleMap != null) {

            Set<String> markerKeys = markerList.keySet();
            int pokemonFound = 0;
            for (CatchablePokemon poke : pokeList) {

                if(!markerKeys.contains(poke.getSpawnPointId())) {
                    int resourceID = getResources().getIdentifier("p" + poke.getPokemonId().getNumber(), "drawable", getActivity().getPackageName());
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(poke.getLatitude(), poke.getLongitude()))
                            .title(poke.getPokemonId().name())
                            .icon(BitmapDescriptorFactory.fromResource(resourceID))
                            .anchor(0.5f, 0.5f));

                    //adding pokemons to list to be removed on next search
                    markerList.put(poke.getSpawnPointId(), new PokemonMarkerExtended(poke, marker));
                    pokemonFound++;
                }
            }

            MainActivity.toast.setText(pokemonFound > 0 ? pokemonFound + " new catchable Pokemon have been found." : "No new Pokemon have been found.");
            MainActivity.toast.show();

            updatePokemonMarkers();
        } else {
            MainActivity.toast.setText("The map is not initialized.");
            MainActivity.toast.show();
        }
    }

    public static String getExpirationBreakdown(long millis) {
        if(millis < 0) {
            return "Expired";
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return(String.format("Expires in: %1$d:%2$02ds", minutes, seconds));
    }

    /**
     * Called whenever a CatchablePokemonEvent is posted to the bus. Posted when new catchable pokemon are found.
     *
     * @param event The event information
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CatchablePokemonEvent event) {

        setPokemonMarkers(event.getCatchablePokemon());
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        UiSettings settings = mGoogleMap.getUiSettings();
        settings.setCompassEnabled(true);
        settings.setTiltGesturesEnabled(true);
        settings.setMyLocationButtonEnabled(false);
        //Handle long click
        mGoogleMap.setOnMapLongClickListener(this);
        //Disable for now coz is under FAB
        settings.setMapToolbarEnabled(false);
        initMap();
    }

    @Override
    public void onMapLongClick(LatLng position) {
        //Draw user position marker with circle
        drawMarkerWithCircle (position);

        //Sending event to MainActivity
        SearchInPosition sip = new SearchInPosition();
        sip.setPosition(position);
        EventBus.getDefault().post(sip);
    }

    private void drawMarkerWithCircle(LatLng position){
        //Check and eventually remove old marker
        if(userSelectedPositionMarker != null && userSelectedPositionCircle != null){
            userSelectedPositionMarker.remove();
            userSelectedPositionCircle.remove();
        }

        double radiusInMeters = 100.0;
        int strokeColor = 0xff3399FF; // outline
        int shadeColor = 0x4400CCFF; // fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        userSelectedPositionCircle = mGoogleMap.addCircle(circleOptions);

        userSelectedPositionMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(position)
                .title("Position Picked")
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getContext().getResources(),
                        R.drawable.ic_my_location_white_24dp)))
                .anchor(0.5f, 0.5f));
    }

}

