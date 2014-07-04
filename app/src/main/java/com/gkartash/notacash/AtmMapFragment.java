package com.gkartash.notacash;


import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by Gennadiy on 23.06.2014.
 */
public class AtmMapFragment extends MapFragment {

    private static final String TAG = "MapFragment";

    public static final String ARG_URL_FOR_DOWNLOADING = "url_for_downloading";
    public static final String ARG_LOCATION = "location";
    public static final String ARG_PROXIMITY = "proximity";
    public static final double METERS_PER_DEGREE = 111120;

    private GoogleMap map;
    private double proximityInDegrees;
    private Location currentLocation;
    private LatLngBounds atmBounds;       //Allows to move camera to current location and set
                                          // appropriate zoom level
    private Bundle args;
    private ProgressDialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();

        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        map = getMap();
        map.setMyLocationEnabled(true);
        if (!(args == null)) {
            getData(args);
        }



    }

    //getData called by fragment in case of one-pane view, or by underlying activity in
    // case of multi-pane view

    public void getData(Bundle inputData) {
        proximityInDegrees = inputData.getInt(ARG_PROXIMITY) / METERS_PER_DEGREE;
        currentLocation = inputData.getParcelable(ARG_LOCATION);
        String url = inputData.getString(ARG_URL_FOR_DOWNLOADING);
        new GetATMList().execute(url);






    }

    //Add ATMs to map, update camera zoom and location to fit all ATMs
    private void showATM(List<AtmObject> atms) {
        atmBounds = new LatLngBounds(new LatLng(currentLocation.getLatitude() -
                proximityInDegrees, currentLocation.getLongitude() - proximityInDegrees),
                new LatLng(currentLocation.getLatitude() + proximityInDegrees,
                        currentLocation.getLongitude() + proximityInDegrees));
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(atmBounds, 0));
                map.setOnCameraChangeListener(null);

            }
        });

        for (AtmObject atm:atms) {
            map.addMarker(new MarkerOptions()
                    .position(atm.getLocation())
                    .title(atm.getBank())
                    .snippet(atm.getAddress() + "\n" + atm.getLocationType() + "\n" + atm
                            .getWorkTime()));



        }

    }


    //Background download and parsing of JSONArray
    private class GetATMList extends AsyncTask<String, Void, List<AtmObject>> {
        @Override
        protected List<AtmObject> doInBackground(String... strings) {
            return AtmManager.getAtmListFromURL(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.atm_list));
            dialog.setIndeterminate(true);
            dialog.setTitle(getString(R.string.progress_title));
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<AtmObject> atmObjects) {
            super.onPostExecute(atmObjects);
            dialog.dismiss();
            showATM(atmObjects);
        }
    }
}
