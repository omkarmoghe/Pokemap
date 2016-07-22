package com.omkarmoghe.pokemap.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.omkarmoghe.pokemap.R;
import com.pokegoapi.api.map.Point;
import com.pokegoapi.api.map.Pokemon.CatchablePokemon;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapWrapperFragment.LocationRequestListener} interface
 * to handle interaction events.
 * Use the {@link MapWrapperFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapWrapperFragment extends Fragment implements OnMapReadyCallback,
                                                            ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 703;

    private LocationRequestListener mListener;

    private View mView;
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap          mGoogleMap;

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
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment if the view is not null
        if (mView == null) mView = inflater.inflate(R.layout.fragment_map_wrapper, container, false);
        else {

        }

        // build the map
        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mSupportMapFragment).commit();
        }

        if (mGoogleMap == null) {
            mSupportMapFragment.getMapAsync(this);
        }

        FloatingActionButton locationFab = (FloatingActionButton) mView.findViewById(R.id.location_fab);
        locationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION_PERMISSION_REQUEST)) {
                    Location myLocation = mListener.requestLocation();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15)
                                            );
                    Toast.makeText(getContext(), "Found you!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationRequestListener) {
            mListener = (LocationRequestListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //if (mGoogleMap == null) mSupportMapFragment.getMapAsync(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    }

    private boolean checkPermission(String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Request them if not enabled
            ActivityCompat.requestPermissions(getActivity(), new String[] {permission}, requestCode);

            return false;
        } else {
            // do the necessary dank shit
            switch (permission) {
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    mGoogleMap.setMyLocationEnabled(true);
                    break;

            }

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // TODO: test all this shit on a 6.0+ phone lmfao

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap.setMyLocationEnabled(true);
                }
                break;

        }

        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface LocationRequestListener {
        Location requestLocation();
    }

    public void setPokemonMarkers(final List<CatchablePokemon> pokeList){

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (CatchablePokemon poke : pokeList) {
                        int resourceID = getResources().getIdentifier("p" + poke.getPokemonId().getNumber(), "mipmap", getActivity().getPackageName());
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(poke.getLatitude(), poke.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(resourceID))
                                .snippet(poke.getPokemonId().name())
                        );
                    }
                }
            });

        }

    public void setSpawnPoints(final List<Point> points){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int resourceID = getResources().getIdentifier("p1", "mipmap", getActivity().getPackageName());
                for(Point point : points) {
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(point.getLatitude(), point.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.p1))
                    );
                }
            }
        });

    }
}

