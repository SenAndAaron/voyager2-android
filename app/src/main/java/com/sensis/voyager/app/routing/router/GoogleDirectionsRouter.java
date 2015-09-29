package com.sensis.voyager.app.routing.router;

import android.util.Log;

import com.sensis.voyager.app.routing.Route;
import com.sensis.voyager.app.routing.google.DirectionServiceURLBuilder;
import com.sensis.voyager.app.routing.google.GoogleParser;

import org.json.JSONException;

import java.io.IOException;

public class GoogleDirectionsRouter extends AbstractRouter {

    private static final String DIRECTIONS_API_URL = "http://maps.googleapis.com/maps/api/directions/json?";

    private GoogleDirectionsRouter(Builder builder) {
        super(builder);
    }

    @Override
    protected Route doInBackground(Void... params) {
        String url = DirectionServiceURLBuilder.build(getOrigin(), getDestination(), getTravelMode(), getUnitOption(), getWaypoints());

        try {
            // Performs the call to Google Maps API to acquire routing data and
            // deserializes it to a format the map can display.
            return GoogleParser.parse(url);
        } catch (IOException | JSONException e) {
            Log.e("GoogleDirectionsRouter", e.getMessage(), e);
            return null;
        }
    }

    public static class Builder extends AbstractRouter.Builder<Builder> {
        public GoogleDirectionsRouter build() {
            return new GoogleDirectionsRouter(this);
        }
    }
}
