package com.sensis.voyager.app.routing.google;


import com.google.android.gms.maps.model.LatLng;
import com.sensis.voyager.app.preferences.TravelMode;
import com.sensis.voyager.app.preferences.UnitOption;

import java.util.ArrayList;
import java.util.List;

public class DirectionServiceURLBuilder {
    private static final String DIRECTIONS_API_URL = "http://maps.googleapis.com/maps/api/directions/json?";

    public static String buildWithoutWaypoints(LatLng origin, LatLng destination, TravelMode travelMode, UnitOption unitOption) {
        return build(origin, destination, travelMode, unitOption, null);
    }

    public static String build(LatLng origin, LatLng destination, TravelMode travelMode, UnitOption unitOption, List<LatLng> waypoints) {
        StringBuilder stringBuilder = new StringBuilder(DIRECTIONS_API_URL);

        // Origin
        stringBuilder.append("origin=");
        stringBuilder.append(origin.latitude);
        stringBuilder.append(',');
        stringBuilder.append(origin.longitude);

        // Destination
        stringBuilder.append("&destination=");
        stringBuilder.append(destination.latitude);
        stringBuilder.append(',');
        stringBuilder.append(destination.longitude);

        // Travel mode
        if (travelMode != null) {
            stringBuilder.append("&mode=");
            stringBuilder.append(travelMode.getName());
        }

        // Units
        if (unitOption != null && unitOption != UnitOption.DEFAULT) {
            stringBuilder.append("&units=");
            stringBuilder.append(unitOption.getName());
        }

        // Waypoints
        if (waypoints != null && !waypoints.isEmpty()) {
            stringBuilder.append("&waypoints=optimize:true|");
            int numberOfWaypoints = waypoints.size();
            for (int i = 0; i < numberOfWaypoints; i++) {
                LatLng waypoint = waypoints.get(i);
                stringBuilder.append(waypoint.latitude);
                stringBuilder.append(",");
                stringBuilder.append(waypoint.longitude);

                if (i < numberOfWaypoints - 1) {
                    stringBuilder.append("|");
                }
            }
        }

        return stringBuilder.toString();
    }

}
