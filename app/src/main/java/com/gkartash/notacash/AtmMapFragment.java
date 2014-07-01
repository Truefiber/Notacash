package com.gkartash.notacash;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Gennadiy on 23.06.2014.
 */
public class AtmMapFragment extends MapFragment {

    private static final String TAG = "MapFragment";

    public static final String ARG_LIST_OF_ATM_LOCATIONS = "list_of_atm_locations";
    public static final String ARG_USED_LOCATION = "used_location";
    public static final String ARG_PROXIMITY = "proximity";
    public static final double METERS_PER_DEGREE = 111120;

    private GoogleMap map;
    private double proximityInDegrees;
    private Location currentLocation;
    private LatLngBounds atmBouns;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        proximityInDegrees = args.getInt(ARG_PROXIMITY) / METERS_PER_DEGREE;
        currentLocation = args.getParcelable(ARG_USED_LOCATION);
        atmBouns = new LatLngBounds(new LatLng(currentLocation.getLatitude() -
                proximityInDegrees, currentLocation.getLongitude() - proximityInDegrees),
                new LatLng(currentLocation.getLatitude() + proximityInDegrees,
                        currentLocation.getLongitude() + proximityInDegrees));
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        map = getMap();
        map.setMyLocationEnabled(true);
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(atmBouns, 0));
                map.setOnCameraChangeListener(null);

            }
        });


    }
}
