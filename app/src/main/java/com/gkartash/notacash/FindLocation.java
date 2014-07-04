package com.gkartash.notacash;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gennadiy on 22.06.2014.
 * One-time location update.
 *
 */
public class FindLocation {

    private static final String TAG = "FindLocation";

    private static final int TIMEOUT = 5000; //milliseconds before returning last known location
    FixedLocation result;
    Timer fixTimer;                          //will return last known location after timeout
    LocationManager lm;
    boolean isGpsEnabled = false;
    boolean isNetworkEnabled = false;

    public boolean fixLocation(Context context, FixedLocation fixedLocation) {

        result = fixedLocation;

        if (lm == null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            Log.d(TAG, "Location providers are not permitted, " + e);
        }

        if (!isGpsEnabled && !isNetworkEnabled) {
            return false;
        }

        if (isGpsEnabled) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
        }
        if (isNetworkEnabled) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
        }

        fixTimer = new Timer();
        fixTimer.schedule(new OutOfTimeLocation(), TIMEOUT);
        return true;


    }

    LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            fixTimer.cancel();
            result.onFixedLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(networkListener);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    LocationListener networkListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            fixTimer.cancel();
            result.onFixedLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(gpsListener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private class OutOfTimeLocation extends TimerTask {

        @Override
        public void run() {
            lm.removeUpdates(gpsListener);
            lm.removeUpdates(networkListener);
            Location networkLocation = null, gpsLocation = null;
            if (isGpsEnabled) {
                gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (isNetworkEnabled) {
                networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (gpsLocation != null && networkLocation != null) {
                if (gpsLocation.getTime() > networkLocation.getTime()) {
                    result.onFixedLocation(gpsLocation);
                } else {
                    result.onFixedLocation(networkLocation);
                }
                return;
            }

            if (gpsLocation != null) {
                result.onFixedLocation(gpsLocation);
                return;
            }

            if (networkLocation != null) {
                result.onFixedLocation(networkLocation);
                return;
            }
            result.onFixedLocation(null);
        }
    }

    public static abstract class FixedLocation {
        public abstract void onFixedLocation(Location location);
    }

}
