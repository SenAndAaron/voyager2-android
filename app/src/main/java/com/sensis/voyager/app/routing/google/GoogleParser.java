package com.sensis.voyager.app.routing.google;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sensis.voyager.app.routing.Leg;
import com.sensis.voyager.app.routing.Route;
import com.sensis.voyager.app.routing.Step;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GoogleParser {

    /**
     * Parses a url pointing to a Google JSON object to a Route object.
     */
    public static Route parse(String feedUrl) throws IOException, JSONException {
        // Turn the stream into a string
        String result = convertStreamToString(getInputStream(feedUrl));
        if (result == null) {
            return null;
        }
        Log.d("GoogleParser", result);

        Route route = new Route();
        // Transform the string into a JSON object
        JSONObject json = new JSONObject(result);
        JSONObject jsonRoute = json.getJSONArray("routes").getJSONObject(0);

        // Get the bounds - northeast and southwest
        JSONObject jsonBounds = jsonRoute.getJSONObject("bounds");
        JSONObject jsonNortheast = jsonBounds.getJSONObject("northeast");
        LatLng northEast = new LatLng(jsonNortheast.getDouble("lat"), jsonNortheast.getDouble("lng"));
        JSONObject jsonSouthwest = jsonBounds.getJSONObject("southwest");
        LatLng southWest = new LatLng(jsonSouthwest.getDouble("lat"), jsonSouthwest.getDouble("lng"));
        route.setLatLgnBounds(northEast, southWest);

        // Get the order of the waypoints
        JSONArray jsonWaypointOrder = jsonRoute.getJSONArray("waypoint_order");
        int numberOfWaypoints = jsonWaypointOrder.length();
        int[] waypointOrder = new int[numberOfWaypoints];
        for (int i = 0; i < numberOfWaypoints; i++) {
            waypointOrder[i] = jsonWaypointOrder.getInt(i);
        }
        route.setWaypointOrder(waypointOrder);

        // Retrieve and decode the polyline
        route.addPoints(decodePolyLine(jsonRoute.getJSONObject("overview_polyline").getString("points")));

        // Get copyright notice (Google terms of service requirement)
        route.setCopyright(jsonRoute.getString("copyrights"));

        // Get any warnings provided (Google terms of service requirement)
        JSONArray warnings = jsonRoute.getJSONArray("warnings");
        if (!warnings.isNull(0)) {
            route.setWarning(warnings.getString(0));
        }

        // Loop through the legs for this route
        JSONArray legs = jsonRoute.getJSONArray("legs");
        for (int legIndex = 0; legIndex < legs.length(); legIndex++) {
            Leg leg = new Leg();
            JSONObject jsonLeg = legs.getJSONObject(legIndex);

            // Get distance and time estimation
            leg.setDurationText(jsonLeg.getJSONObject("duration").getString("text"));
            leg.setDurationValue(Double.parseDouble(leg.getDurationText().split(" ")[0]));
            leg.setDistanceText(jsonLeg.getJSONObject("distance").getString("text"));
            leg.setDistanceValue(Double.parseDouble(leg.getDistanceText().split(" ")[0]));

            // Get the start and end locations and addresses
            leg.setStartAddress(jsonLeg.getString("start_address"));
            leg.setEndAddress(jsonLeg.getString("end_address"));
            JSONObject startOfLeg = jsonLeg.getJSONObject("start_location");
            leg.setStartLocation(new LatLng(startOfLeg.getDouble("lat"), startOfLeg.getDouble("lng")));
            JSONObject endOfLeg = jsonLeg.getJSONObject("end_location");
            leg.setEndLocation(new LatLng(endOfLeg.getDouble("lat"), endOfLeg.getDouble("lng")));

            // Loop through the steps for this leg
            JSONArray steps = jsonLeg.getJSONArray("steps");
            for (int stepIndex = 0; stepIndex < steps.length(); stepIndex++) {
                Step step = new Step();
                JSONObject jsonStep = steps.getJSONObject(stepIndex);

                // Get the start and end location for this step
                JSONObject start = jsonStep.getJSONObject("start_location");
                step.setStart(new LatLng(start.getDouble("lat"), start.getDouble("lng")));
                JSONObject end = jsonStep.getJSONObject("end_location");
                step.setEnd(new LatLng(end.getDouble("lat"), end.getDouble("lng")));

                // Strip html from Google directions and set as turn instruction
                step.setInstruction(jsonStep.getString("html_instructions").replaceAll("<(.*?)*>", ""));

                leg.addStep(step);
            }

            route.addLeg(leg);
        }

        return route;
    }

    private static String convertStreamToString(InputStream input) throws IOException {
        if (input == null) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } finally {
            input.close();
        }
        return stringBuilder.toString();
    }

    /**
     * Decode a polyline string into a list of LatLng.
     * See: https://developers.google.com/maps/documentation/utilities/polylinealgorithm
     */
    private static List<LatLng> decodePolyLine(String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(lat / 100000d, lng / 100000d));
        }

        return decoded;
    }

    private static InputStream getInputStream(String feedUrl) throws IOException {
        URL url = new URL(feedUrl);
        return url.openConnection().getInputStream();
    }
}