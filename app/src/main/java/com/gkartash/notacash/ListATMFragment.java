package com.gkartash.notacash;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gennadiy on 22.06.2014.
 */
public class ListATMFragment extends ListFragment {

    private static final String TAG = "ListATMFragment";
    Button fetchButton;
    GetData getData;
    List<AtmObject> listAtm;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getData = new GetData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.listfragment, container, false);
        //fetchButton = (Button) view.findViewById(android.R.id.empty);
        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
            }
        });

        return null;
    }

    public void fetchData() {
        if (!isConnected()) {
            Toast.makeText(getActivity(), R.string.internetconnection, Toast.LENGTH_SHORT).show();
            return;
        }

        FindLocation loc = new FindLocation();

        if (!loc.fixLocation(getActivity(), fixedLocation)) {
            Toast.makeText(getActivity(), R.string.location_provider_error, Toast.LENGTH_SHORT).show();
            return;
        }







    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    FindLocation.FixedLocation fixedLocation = new FindLocation.FixedLocation() {
        @Override
        public void onFixedLocation(Location location) {
            getData.execute(location);
        }
    };

    private class GetData extends AsyncTask<Location,Void, ArrayList<AtmObject>> {

        private static final String formatUrl = "http://notacash" +
                ".com/api/search/0/0/%f/%f/%d";



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<AtmObject> doInBackground(Location... locations) {
            String url = String.format(formatUrl, locations[0].getLatitude(),
                    locations[0].getLongitude(), 300);

            Log.d(TAG, "Url: " + url);

            return null;


        }
    }
}
