package com.omkarmoghe.pokemap.views.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
<<<<<<< HEAD
import android.content.res.Resources;
=======
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
>>>>>>> refs/remotes/omkarmoghe/dev
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
<<<<<<< HEAD
import android.util.Log;
=======
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
>>>>>>> refs/remotes/omkarmoghe/dev
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapAppPreferences;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;
import com.omkarmoghe.pokemap.controllers.map.LocationManager;
import com.omkarmoghe.pokemap.models.events.ClearMapEvent;
import com.omkarmoghe.pokemap.models.events.PokestopsEvent;
import com.omkarmoghe.pokemap.models.map.PokemonMarkerExtended;
import com.omkarmoghe.pokemap.models.events.CatchablePokemonEvent;
import com.omkarmoghe.pokemap.models.events.SearchInPosition;
import com.omkarmoghe.pokemap.models.map.PokestopMarkerExtended;
import com.omkarmoghe.pokemap.models.map.SearchParams;
import com.omkarmoghe.pokemap.views.MainActivity;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
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

    private PokemapAppPreferences mPref;
    private View mView;
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;
    private Location mLocation = null;
    private Marker userSelectedPositionMarker = null;
    private ArrayList<Circle> userSelectedPositionCircles = new ArrayList<>();
    private HashMap<String, PokemonMarkerExtended> markerList = new HashMap<>();
    private HashMap<String, PokestopMarkerExtended> pokestopsList = new HashMap<>();

    public static Snackbar pokeSnackbar;
    public static int pokemonFound = 0;
    public static int positionNum = 0;

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
<<<<<<< HEAD
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
=======
        mPref = new PokemapSharedPreferences(getContext());
>>>>>>> refs/remotes/omkarmoghe/dev
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        updateMarkers();
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
                } else {
                    mLocation = location;
                }
            }

            @Override
            public void onLocationFetchFailed(@Nullable ConnectionResult connectionResult) {
                showLocationFetchFailed();
            }
        });
        // Inflate the layout for this fragment if the view is not null
        if (mView == null)
            mView = inflater.inflate(R.layout.fragment_map_wrapper, container, false);
<<<<<<< HEAD
=======
        else {

        }
>>>>>>> refs/remotes/omkarmoghe/dev

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
<<<<<<< HEAD

                    MainActivity.toast.setText(getString(R.string.toast_user_found));
                    MainActivity.toast.show();
                } else {

                    MainActivity.toast.setText(getString(R.string.toast_localization_waiting));
                    MainActivity.toast.show();
=======
                } else {
                    showLocationFetchFailed();
>>>>>>> refs/remotes/omkarmoghe/dev
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

    private void initMap() {
<<<<<<< HEAD
        if (mLocation != null && mGoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
=======
        pokeSnackbar = Snackbar.make(getView(), "", Snackbar.LENGTH_LONG);
        if (mLocation != null && mGoogleMap != null) {
            if (ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Enable Location Permission")
                        .setMessage("Please enable location permission to use this application")
                        .setPositiveButton("OK", null)
                        .show();
>>>>>>> refs/remotes/omkarmoghe/dev
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15));
<<<<<<< HEAD
            MainActivity.toastMe(getString(R.string.toast_user_found));
=======
        } else {
            showLocationFetchFailed();
>>>>>>> refs/remotes/omkarmoghe/dev
        }
    }

    private void clearMarkers() {
        if (mGoogleMap != null) {
            if (markerList != null && !markerList.isEmpty()) {
                for (Iterator<Map.Entry<String, PokemonMarkerExtended>> pokemonIterator = markerList.entrySet().iterator(); pokemonIterator.hasNext(); ) {
                    Map.Entry<String, PokemonMarkerExtended> pokemonEntry = pokemonIterator.next();
                    pokemonEntry.getValue().getMarker().remove();
                    pokemonIterator.remove();
                }
            }
            if (pokestopsList != null && !pokestopsList.isEmpty()) {
                for (Iterator<Map.Entry<String, PokestopMarkerExtended>> pokestopIterator = pokestopsList.entrySet().iterator(); pokestopIterator.hasNext(); ) {
                    Map.Entry<String, PokestopMarkerExtended> pokestopEntry = pokestopIterator.next();
                    pokestopEntry.getValue().getMarker().remove();
                    pokestopIterator.remove();
                }
            }

            if (userSelectedPositionCircles != null && !userSelectedPositionCircles.isEmpty()) {
                for (Circle circle : userSelectedPositionCircles) {
                    circle.remove();
                }
                userSelectedPositionCircles.clear();
            }
        }

    }

    private void updateMarkers() {
        if (mGoogleMap != null) {
            if (markerList != null && !markerList.isEmpty()) {
                for (Iterator<Map.Entry<String, PokemonMarkerExtended>> pokemonIterator = markerList.entrySet().iterator(); pokemonIterator.hasNext(); ) {
                    Map.Entry<String, PokemonMarkerExtended> pokemonEntry = pokemonIterator.next();
                    CatchablePokemon catchablePokemon = pokemonEntry.getValue().getCatchablePokemon();
                    Marker marker = pokemonEntry.getValue().getMarker();
                    long millisLeft = catchablePokemon.getExpirationTimestampMs() - System.currentTimeMillis();
                    if (millisLeft < 0) {
                        marker.remove();
                        pokemonIterator.remove();
                    } else {
                        marker.setSnippet(getExpirationBreakdown(millisLeft));
                        if (marker.isInfoWindowShown()) {
                            marker.showInfoWindow();
                        }
                    }
                }
            }
            if (pokestopsList != null && !pokestopsList.isEmpty() && mPref.getShowPokestops()) {
                for (Iterator<Map.Entry<String, PokestopMarkerExtended>> pokestopIterator = pokestopsList.entrySet().iterator(); pokestopIterator.hasNext(); ) {
                    Map.Entry<String, PokestopMarkerExtended> pokestopEntry = pokestopIterator.next();
                    Pokestop pokestop = pokestopEntry.getValue().getPokestop();
                    Marker marker = pokestopEntry.getValue().getMarker();
                    int pstopID = getResources().getIdentifier("pstop", "drawable", getActivity().getPackageName());
                    int pstopLuredID = getResources().getIdentifier("pstop_lured", "drawable", getActivity().getPackageName());
                    marker.setIcon(BitmapDescriptorFactory.fromResource(pokestop.hasLurePokemon() ? pstopLuredID: pstopID));
                }
            } else if(pokestopsList != null && !pokestopsList.isEmpty() && !mPref.getShowPokestops()){
                for (Iterator<Map.Entry<String, PokestopMarkerExtended>> pokestopIterator = pokestopsList.entrySet().iterator(); pokestopIterator.hasNext(); ) {
                    Map.Entry<String, PokestopMarkerExtended> pokestopEntry = pokestopIterator.next();
                    Pokestop pokestop = pokestopEntry.getValue().getPokestop();
                    Marker marker = pokestopEntry.getValue().getMarker();
                    marker.remove();
                    pokestopIterator.remove();
                }

            }


            if (!mPref.getShowScannedPlaces() && userSelectedPositionCircles != null && !userSelectedPositionCircles.isEmpty()) {
                for (Circle circle : userSelectedPositionCircles) {
                    circle.remove();
                }
                userSelectedPositionCircles.clear();
            }
        }

    }

    private void setPokestopsMarkers(final Collection<Pokestop> pokestops){
        if (mGoogleMap != null) {

            if(pokestops != null && mPref.getShowPokestops()) {
                Set<String> markerKeys = pokestopsList.keySet();
                int pstopID = getResources().getIdentifier("pstop", "drawable", getActivity().getPackageName());
                int pstopLuredID = getResources().getIdentifier("pstop_lured", "drawable", getActivity().getPackageName());
                for (Pokestop pokestop : pokestops) {
                    if (!markerKeys.contains(pokestop.getId())) {
                        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(pokestop.getLatitude(), pokestop.getLongitude()))
                                .title(getString(R.string.pokestop))
                                .icon(BitmapDescriptorFactory.fromResource(pokestop.hasLurePokemon() ? pstopLuredID: pstopID))
                                .anchor(0.5f, 0.5f));

                        //adding pokemons to list to be removed on next search
                        pokestopsList.put(pokestop.getId(), new PokestopMarkerExtended(pokestop, marker));
                    }
                }
            }
            updateMarkers();

        } else {
            showMapNotInitializedError();
        }
    }

    private void setPokemonMarkers(final List<CatchablePokemon> pokeList){
        positionNum++;
        int markerSize = getResources().getDimensionPixelSize(R.dimen.pokemon_marker);
        if (mGoogleMap != null) {

            Set<String> markerKeys = markerList.keySet();
            for (final CatchablePokemon poke : pokeList) {

                if(!markerKeys.contains(poke.getSpawnPointId())) {
                    //Showing images using glide
                    Glide.with(getActivity())
                            .load("http://serebii.net/pokemongo/pokemon/"+getCorrectPokemonImageId(poke.getPokemonId().getNumber())+".png")
                            .asBitmap()
                            .skipMemoryCache(false)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new SimpleTarget<Bitmap>(markerSize, markerSize) { // Width and height FIXME: Maybe get different sizes based on devices DPI? this need tests
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                    //Setting marker since we got image
                                    //int resourceID = getResources().getIdentifier("p" + poke.getPokemonId().getNumber(), "drawable", getActivity().getPackageName());
                                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(poke.getLatitude(), poke.getLongitude()))
                                            .title(poke.getPokemonId().name())
                                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                            .anchor(0.5f, 0.5f));
                                    //adding pokemons to list to be removed on next search
                                    markerList.put(poke.getSpawnPointId(), new PokemonMarkerExtended(poke, marker));
                                }
                            });
                    //Increase founded pokemon counter
                    pokemonFound++;
                }
<<<<<<< HEAD
                markerList = new ArrayList<>(); //cleaning the array
=======
>>>>>>> refs/remotes/omkarmoghe/dev
            }
            if(getView() != null) {
                if(positionNum != LOCATION_PERMISSION_REQUEST) {
                    String text = " Searching...." + pokemonFound + " Pokemon found";
                    pokeSnackbar.setText(text);
                    pokeSnackbar.show();

<<<<<<< HEAD
            for (CatchablePokemon poke : pokeList) {
                int resourceID = getResources().getIdentifier("p" + poke.getPokemonId().getNumber(), "drawable", getActivity().getPackageName());
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(resourceID);

                String breakdown = getDurationBreakdown(poke.getExpirationTimestampMs() - System.currentTimeMillis());
                String snippet = getString(R.string.pokemon_disappearing_in)+ breakdown;

                String pokeName = poke.getPokemonId().name();
                int resId = getPokemonNameId(pokeName);
                String title = resId == -1 ? pokeName : getString(resId);

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(poke.getLatitude(), poke.getLongitude()))
                        .title(title)
                        .snippet(snippet)
                        .icon(icon));

                marker.showInfoWindow();
                //adding pokemons to list to be removed on next search
                markerList.add(marker);
=======
                }
                else {
                    String text = pokemonFound > 0 ? pokemonFound + " new catchable Pokemon have been found." : "No new Pokemon have been found.";
                    pokeSnackbar.setText(text);
                    pokeSnackbar.show();
                }
>>>>>>> refs/remotes/omkarmoghe/dev
            }
            updateMarkers();
        } else {
<<<<<<< HEAD
            MainActivity.toastMe(getString(R.string.toast_map_not_initialized));
=======
            showMapNotInitializedError();
        }
    }

    //Getting correct pokemon Id eg: 1 must be 001, 10 must be 010
    private String getCorrectPokemonImageId (int pokemonId){
        String actualNumber = String.valueOf(pokemonId);
        if(pokemonId < 10){
            return "00" + actualNumber;
        }else if(pokemonId < 100) {
            return "0" + actualNumber;
        }else {
            return actualNumber;
>>>>>>> refs/remotes/omkarmoghe/dev
        }
    }
    private int getPokemonNameId(String pokeName){
        try {
            Class res = R.string.class;
            Field field = res.getField(pokeName.toLowerCase());
            int id = field.getInt(null);

<<<<<<< HEAD
            return id;
        }
        catch (Exception e) {
            Log.e("MyTag", "Failure to get id.", e);
            return -1;
        }
    }

    public String getDurationBreakdown(long millis) {
=======
    private void showMapNotInitializedError() {
        if(getView() != null){
            Snackbar.make(getView(), "Problem Initializing Google Map", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showLocationFetchFailed() {
        if(getView() != null){
            Snackbar.make(getView(), "Failed to Find GPS Location", Snackbar.LENGTH_SHORT).show();
        }
    }

    public static String getExpirationBreakdown(long millis) {
>>>>>>> refs/remotes/omkarmoghe/dev
        if(millis < 0) {
            return getString(R.string.pokemon_expired);
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

<<<<<<< HEAD
        StringBuilder sb = new StringBuilder(64);
        sb.append(minutes);
        sb.append(getString(R.string.minutes));
        sb.append(seconds);
        sb.append(getString(R.string.seconds));

        return(sb.toString());
=======
        return(String.format("Expires in: %1$d:%2$02ds", minutes, seconds));
>>>>>>> refs/remotes/omkarmoghe/dev
    }

    /**
     * Called whenever a CatchablePokemonEvent is posted to the bus. Posted when new catchable pokemon are found.
     *
     * @param event The event information
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CatchablePokemonEvent event) {
<<<<<<< HEAD
        MainActivity.toastMe(event.getCatchablePokemon().size() + getString(R.string.pokemon_found_new));
=======
>>>>>>> refs/remotes/omkarmoghe/dev
        setPokemonMarkers(event.getCatchablePokemon());
    }

    /**
     * Called whenever a ClearMapEvent is posted to the bus. Posted when the user wants to clear map of any pokemon or marker.
     *
     * @param event The event information
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ClearMapEvent event) {
        clearMarkers();
    }

    /**
     * Called whenever a PokestopsEvent is posted to the bus. Posted when new pokestops are found.
     *
     * @param event The event information
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PokestopsEvent event) {

        setPokestopsMarkers(event.getPokestops());
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
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
    }

    @Override
    public void onMapLongClick(LatLng position) {
        //Draw user position marker with circle
        drawMarkerWithCircle (position);

        //Sending event to MainActivity
        SearchInPosition sip = new SearchInPosition();
        sip.setPosition(position);
        EventBus.getDefault().post(sip);

        mView.findViewById(R.id.layoutSuggestions).setVisibility(View.GONE);
    }

    private void drawMarkerWithCircle(LatLng position){
        if (mGoogleMap != null) {


            //Check and eventually remove old marker
            if (userSelectedPositionMarker != null && userSelectedPositionCircles != null) {
                userSelectedPositionMarker.remove();
                for (Circle circle : userSelectedPositionCircles) {
                    circle.remove();
                }
                userSelectedPositionCircles.clear();
            }

            if(mPref.getShowScannedPlaces()) {
                double radiusInMeters = 100.0;
                int strokeColor = 0x4400CCFF; // outline
                int shadeColor = 0x4400CCFF; // fill

                SearchParams params = new SearchParams(SearchParams.DEFAULT_RADIUS * 3, new LatLng(position.latitude, position.longitude));
                List<LatLng> list = params.getSearchArea();
                for (LatLng p : list) {
                    CircleOptions circleOptions = new CircleOptions().center(new LatLng(p.latitude, p.longitude)).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                    userSelectedPositionCircles.add(mGoogleMap.addCircle(circleOptions));

                }
            }

<<<<<<< HEAD
        userSelectedPositionMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(getString(R.string.position_picked))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
=======
            userSelectedPositionMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("Position Picked")
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getContext().getResources(),
                            R.drawable.ic_my_location_white_24dp)))
                    .anchor(0.5f, 0.5f));
        } else {
            showMapNotInitializedError();
        }
>>>>>>> refs/remotes/omkarmoghe/dev
    }

}

