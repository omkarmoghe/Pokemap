package com.omkarmoghe.pokemap.views.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.omkarmoghe.pokemap.controllers.MarkerRefreshController;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapAppPreferences;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;
import com.omkarmoghe.pokemap.controllers.map.LocationManager;
import com.omkarmoghe.pokemap.helpers.RemoteImageLoader;
import com.omkarmoghe.pokemap.models.events.CatchablePokemonEvent;
import com.omkarmoghe.pokemap.models.events.ClearMapEvent;
import com.omkarmoghe.pokemap.models.events.GymsEvent;
import com.omkarmoghe.pokemap.models.events.MarkerUpdateEvent;
import com.omkarmoghe.pokemap.models.events.PokestopsEvent;
import com.omkarmoghe.pokemap.models.events.SearchInPosition;
import com.omkarmoghe.pokemap.models.map.GymMarkerExtended;
import com.omkarmoghe.pokemap.models.map.PokemonMarkerExtended;
import com.omkarmoghe.pokemap.models.map.PokestopMarkerExtended;
import com.omkarmoghe.pokemap.util.PokemonIdUtils;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import POGOProtos.Enums.TeamColorOuterClass;
import POGOProtos.Map.Fort.FortDataOuterClass;

import POGOProtos.Enums.PokemonIdOuterClass;

/**
 * A simple {@link Fragment} subclass.
 * <p>
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
    private Map<String, GymMarkerExtended> gymsList = new HashMap<>();
    Map<Integer, String> gymTeamImageUrls = new HashMap<>();
    String lurePokeStopImageUrl = "http://i.imgur.com/2BI3Cqv.png";
    String pokeStopImageUrl = "http://i.imgur.com/pmLrx3R.png";

    private List<Circle> userSelectedPositionCircles = new ArrayList<>();
    private Map<String, PokemonMarkerExtended> markerList = new HashMap<>();
    private Map<String, PokestopMarkerExtended> pokestopsList = new HashMap<>();

    private Set<PokemonIdOuterClass.PokemonId> showablePokemonIDs = new HashSet<>();

    public static Snackbar pokeSnackbar;
    public static int pokemonFound = 0;
    public static int positionNum = 0;

    public MapWrapperFragment() {

        gymTeamImageUrls.put(TeamColorOuterClass.TeamColor.NEUTRAL_VALUE, "http://i.imgur.com/If3mHMM.png");
        gymTeamImageUrls.put(TeamColorOuterClass.TeamColor.BLUE_VALUE, "http://i.imgur.com/ElM6sqb.png");
        gymTeamImageUrls.put(TeamColorOuterClass.TeamColor.RED_VALUE, "http://i.imgur.com/wO13iJ0.png");
        gymTeamImageUrls.put(TeamColorOuterClass.TeamColor.YELLOW_VALUE, "http://i.imgur.com/F8Jq1dc.png");
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
        mPref = new PokemapSharedPreferences(getContext());
        showablePokemonIDs = mPref.getShowablePokemonIDs();
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
                } else {
                    showLocationFetchFailed();
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
        pokeSnackbar = Snackbar.make(getView(), "", Snackbar.LENGTH_LONG);
        if (mLocation != null && mGoogleMap != null) {
            if (ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.enable_location_permission_title))
                        .setMessage(getString(R.string.enable_location_permission_message))
                        .setPositiveButton(getString(R.string.button_ok), null)
                        .show();
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15));
        } else {
            showLocationFetchFailed();
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

            if (gymsList != null && !gymsList.isEmpty()) {
                for (Iterator<Map.Entry<String, GymMarkerExtended>> gymIterator = gymsList.entrySet().iterator(); gymIterator.hasNext(); ) {
                    Map.Entry<String, GymMarkerExtended> gymEntry = gymIterator.next();
                    gymEntry.getValue().getMarker().remove();
                    gymIterator.remove();
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

                    if (!showablePokemonIDs.contains(catchablePokemon.getPokemonId())) {
                        marker.remove();
                        pokemonIterator.remove();
                    } else {
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
            }
            if (pokestopsList != null && !pokestopsList.isEmpty() && mPref.getShowPokestops()) {

                for (Iterator<Map.Entry<String, PokestopMarkerExtended>> pokestopIterator = pokestopsList.entrySet().iterator(); pokestopIterator.hasNext(); ) {
                    Map.Entry<String, PokestopMarkerExtended> pokestopEntry = pokestopIterator.next();
                    final Pokestop pokestop = pokestopEntry.getValue().getPokestop();
                    final Marker marker = pokestopEntry.getValue().getMarker();

                    int markerSize = getResources().getDimensionPixelSize(R.dimen.pokestop_marker);

                    RemoteImageLoader.load(
                        pokestop.hasLurePokemon() ? lurePokeStopImageUrl : pokeStopImageUrl,
                        markerSize, markerSize,
                        getActivity(),
                        new RemoteImageLoader.Callback() {
                            @Override
                            public void onFetch(Bitmap bitmap) {

                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                                marker.setIcon(bitmapDescriptor);
                            }
                        }
                    );
                }
            } else if (pokestopsList != null && !pokestopsList.isEmpty() && !mPref.getShowPokestops()) {
                for (Iterator<Map.Entry<String, PokestopMarkerExtended>> pokestopIterator = pokestopsList.entrySet().iterator(); pokestopIterator.hasNext(); ) {
                    Map.Entry<String, PokestopMarkerExtended> pokestopEntry = pokestopIterator.next();
                    Marker marker = pokestopEntry.getValue().getMarker();
                    marker.remove();
                    pokestopIterator.remove();
                }
            }

            if (gymsList != null && !gymsList.isEmpty() && mPref.getShowGyms()) {

                for (Iterator<Map.Entry<String, GymMarkerExtended>> gymIterator = gymsList.entrySet().iterator(); gymIterator.hasNext(); ) {
                    Map.Entry<String, GymMarkerExtended> gymEntry = gymIterator.next();
                    final FortDataOuterClass.FortData gym = gymEntry.getValue().getGym();
                    final Marker marker = gymEntry.getValue().getMarker();

                    int markerSize = getResources().getDimensionPixelSize(R.dimen.gym_marker);

                    RemoteImageLoader.load(
                        gymTeamImageUrls.get(gym.getOwnedByTeam().getNumber()),
                        markerSize, markerSize,
                        getActivity(),
                        new RemoteImageLoader.Callback() {
                            @Override
                            public void onFetch(Bitmap  bitmap) {

                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                                marker.setIcon(bitmapDescriptor);
                            }
                        }
                    );
                }
            } else if(gymsList != null && !gymsList.isEmpty() && !mPref.getShowGyms()){
                for (Iterator<Map.Entry<String, GymMarkerExtended>> gymIterator = gymsList.entrySet().iterator(); gymIterator.hasNext(); ) {
                    Map.Entry<String, GymMarkerExtended> gymEntry = gymIterator.next();
                    Marker marker = gymEntry.getValue().getMarker();
                    marker.remove();
                    gymIterator.remove();
                }
            }


            if (!mPref.getShowScannedPlaces() && userSelectedPositionCircles != null && !userSelectedPositionCircles.isEmpty()) {
                for (Circle circle : userSelectedPositionCircles) {
                    circle.remove();
                }
                userSelectedPositionCircles.clear();
            }
        }
        MarkerRefreshController.getInstance().reset();
    }

    private void setPokestopsMarkers(final Collection<Pokestop> pokestops) {
        if (mGoogleMap != null) {

            int markerSize = getResources().getDimensionPixelSize(R.dimen.pokestop_marker);

            if(pokestops != null && mPref.getShowPokestops()) {
                Set<String> markerKeys = pokestopsList.keySet();

                for (final Pokestop pokestop : pokestops) {

                    if (!markerKeys.contains(pokestop.getId())) {

                            RemoteImageLoader.load(
                            pokestop.hasLurePokemon() ? lurePokeStopImageUrl : pokeStopImageUrl,
                            markerSize, markerSize,
                            getActivity(),
                            new RemoteImageLoader.Callback() {
                                @Override
                                public void onFetch(Bitmap bitmap) {

                                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

                                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(pokestop.getLatitude(), pokestop.getLongitude()))
                                        .title(getString(R.string.pokestop))
                                        .icon(bitmapDescriptor)
                                        .anchor(0.5f, 0.5f));

                                    //adding pokemons to list to be removed on next search
                                    pokestopsList.put(pokestop.getId(), new PokestopMarkerExtended(pokestop, marker));
                                }
                            }
                        );
                    }
                }
            }
            updateMarkers();

        } else {
            showMapNotInitializedError();
        }
    }

    private void setGymsMarkers(final Collection<FortDataOuterClass.FortData> gyms){
        if (mGoogleMap != null) {

            int markerSize = getResources().getDimensionPixelSize(R.dimen.gym_marker);

            if(gyms != null && mPref.getShowGyms()) {

                Set<String> markerKeys = gymsList.keySet();

                for (final FortDataOuterClass.FortData gym : gyms) {

                    if (!markerKeys.contains(gym.getId())) {

                        RemoteImageLoader.load(
                            gymTeamImageUrls.get(gym.getOwnedByTeam().getNumber()),
                            markerSize, markerSize,
                            getActivity(),
                            new RemoteImageLoader.Callback() {
                                @Override
                                public void onFetch(Bitmap bitmap) {

                                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

                                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(gym.getLatitude(), gym.getLongitude()))
                                        .title(getString(R.string.gym))
                                        .icon(bitmapDescriptor)
                                        .anchor(0.5f, 0.5f));

                                    // adding gyms to list to be removed on next search
                                    gymsList.put(gym.getId(), new GymMarkerExtended(gym, marker));
                                }
                            }
                        );
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

                    // checking if we need to show this pokemon
                    PokemonIdOuterClass.PokemonId pokemonId = poke.getPokemonId();
                    if (showablePokemonIDs.contains(pokemonId)) {

                        RemoteImageLoader.load(
                            "http://serebii.net/pokemongo/pokemon/"+PokemonIdUtils.getCorrectPokemonImageId(pokemonId.getNumber())+".png",
                            markerSize, markerSize,
                            getActivity(),
                            new RemoteImageLoader.Callback() {
                                @Override
                                public void onFetch(Bitmap bitmap) {

                                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

                                    //Setting marker since we got image
                                    //int resourceID = getResources().getIdentifier("p" + poke.getPokemonId().getNumber(), "drawable", getActivity().getPackageName());
                                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(poke.getLatitude(), poke.getLongitude()))
                                            .title(PokemonIdUtils.getLocalePokemonName(getContext(), poke.getPokemonId().name()))
                                            .icon(bitmapDescriptor)
                                            .anchor(0.5f, 0.5f));
                                    //adding pokemons to list to be removed on next search
                                    markerList.put(poke.getSpawnPointId(), new PokemonMarkerExtended(poke, marker));
                                }
                            }
                        );

                        //Increase founded pokemon counter
                        pokemonFound++;
                    }
                }
            }
            if (getView() != null) {
                if (positionNum != LOCATION_PERMISSION_REQUEST) {
                    pokeSnackbar.setText(String.format("%s %s %s",getString(R.string.toast_searching), pokemonFound, getString(R.string.toast_pokemon_found_count)));
                    pokeSnackbar.show();

                } else {
                    String text = pokemonFound > 0 ? String.format("%s %s", pokemonFound, getString(R.string.pokemon_found_new)) : getString(R.string.pokemon_found_none);
                    pokeSnackbar.setText(text);
                    pokeSnackbar.show();
                }
            }
            updateMarkers();
        } else {
            showMapNotInitializedError();
        }
    }

    private void removeExpiredMarkers() {

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
                                    MarkerRefreshController.getInstance().notifyTimeToExpiry(millisLeft);}
                        }
                        MarkerRefreshController.getInstance().reset();
                    }
                }

    }


    private void showMapNotInitializedError() {
        if(getView() != null){
            Snackbar.make(getView(), getString(R.string.toast_map_not_initialized), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showLocationFetchFailed() {
        if(getView() != null){
            Snackbar.make(getView(),getString(R.string.toast_no_location), Snackbar.LENGTH_SHORT).show();
        }
    }

    private String getExpirationBreakdown(long millis) {
        if(millis < 0) {
            return getString(R.string.pokemon_expired);
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return(getString(R.string.expiring_in)+String.format("%1$d:%2$02d %3$s", minutes, seconds,getString(R.string.seconds)));
    }

    /**
     * Called whenever a CatchablePokemonEvent is posted to the bus. Posted when new catchable pokemon are found.
     *
     * @param event The event information
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CatchablePokemonEvent event) {
        setPokemonMarkers(event.getCatchablePokemon());
        drawCatchedPokemonCircle(event.getLat(), event.getLongitude());
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

    /**
     * Called whenever a PokestopsEvent is posted to the bus. Posted when new gyms are found.
     *
     * @param event The event information
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GymsEvent event) {

        setGymsMarkers(event.getGyms());
    }

    /**
     * Called whenever a MarkerUpdateEvent is posted to the bus. Posted by {@link MarkerRefreshController} when
     * expired markers need to be removed.
     *
     * @param event
     */

     @Subscribe(threadMode = ThreadMode.MAIN)
     public void onEvent(MarkerUpdateEvent event){
         removeExpiredMarkers();
     }


    private void clearCatchedPokemonCircle() {

        //Check and eventually remove old marker
        if (userSelectedPositionMarker != null && userSelectedPositionCircles != null) {
            userSelectedPositionMarker.remove();
            for (Circle circle : userSelectedPositionCircles) {
                circle.remove();
            }
            userSelectedPositionCircles.clear();
        }
    }

    private void drawCatchedPokemonCircle(double latitude, double longitude) {

        if (mGoogleMap != null) {

            if (mPref.getShowScannedPlaces()) {

                double radiusInMeters = 100.0;
                int strokeColor = 0x4400CCFF; // outline
                int shadeColor = 0x4400CCFF; // fill

                CircleOptions circleOptions = new CircleOptions().center(new LatLng(latitude, longitude)).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                userSelectedPositionCircles.add(mGoogleMap.addCircle(circleOptions));
            }
        }
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

        clearCatchedPokemonCircle();

        //Draw user position marker with circle
        drawMarker(position);

        //Sending event to MainActivity
        SearchInPosition sip = new SearchInPosition();
        sip.setPosition(position);
        EventBus.getDefault().post(sip);

        mView.findViewById(R.id.layoutSuggestions).setVisibility(View.GONE);
    }

    private void drawMarker(LatLng position){
        if (mGoogleMap != null) {

            userSelectedPositionMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(getString(R.string.position_picked))
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getContext().getResources(),
                            R.drawable.ic_my_location_white_24dp)))
                    .anchor(0.5f, 0.5f));
        } else {
            showMapNotInitializedError();
        }
    }
}