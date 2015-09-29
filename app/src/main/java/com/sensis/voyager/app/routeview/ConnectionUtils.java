package com.sensis.voyager.app.routeview;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

public class ConnectionUtils {
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static Location getLastKnownLocation(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = manager.getProviders(true);
        for (String provider : providers) {
            Location location = manager.getLastKnownLocation(provider);
            if (location != null) {
                return location;
            }
        }

        // At this point we've done all we can and no location is returned
        return null;
    }
}
