package com.gkartash.notacash;

import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Gennadiy on 23.06.2014.
 */
public class AtmManager {

    public static final String TAG = "AtmManager";

    private Context mContext;

    Location currentLocation;



    public AtmManager(Context context) {
        mContext = context;
    }

    public List<String> getCategories() {
        return null;
    }

    public List<String> getSubcategories(int category) {
        return null;
    }



    public List<AtmObject> getATMList(int categoty, int subcategory,
                                      int proximity) {

        FindLocation loc = new FindLocation();
        FindLocation.FixedLocation fixedLocation = new FindLocation.FixedLocation() {
            @Override
            public void onFixedLocation(Location location) {
                Log.d(TAG, "Location: " + location);
                currentLocation = location;



            }
        };

        if (!loc.fixLocation(mContext, fixedLocation)) {
            Toast.makeText(mContext, R.string.location_provider_error, Toast.LENGTH_SHORT).show();
            return null;
        }

        return null;
    }

    //check data connection availability
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    public Location getCurrentLocation() {
        return  currentLocation;
    }


}
