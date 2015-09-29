package com.sensis.voyager.app.routing.router;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sensis.voyager.app.preferences.TravelMode;
import com.sensis.voyager.app.preferences.UnitOption;
import com.sensis.voyager.app.routing.Route;
import com.sensis.voyager.app.routing.RoutingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractRouter extends AsyncTask<Void, Void, Route> {
    private TravelMode travelMode;
    private UnitOption unitOption;
    private LatLng origin;
    private LatLng destination;
    private List<LatLng> waypoints;
    private Set<RoutingListener> listeners;

    protected AbstractRouter(Builder builder) {
        setTravelMode(builder.travelMode);
        setUnitOption(builder.unitOption);
        setOrigin(builder.origin);
        setDestination(builder.destination);
        setWaypoints(builder.waypoints);
        setListeners(builder.listeners);
    }

    @Override
    protected void onPreExecute() {
        for (RoutingListener listener : listeners) {
            listener.onRoutingStart();
        }
    }

    @Override
    protected void onPostExecute(Route result) {
        if (result == null) {
            for (RoutingListener listener : listeners) {
                listener.onRoutingFailure();
            }
        } else {
            PolylineOptions polylineOptions = new PolylineOptions();
            for (LatLng point : result.getPoints()) {
                polylineOptions.add(point);
            }

            for (RoutingListener listener : listeners) {
                listener.onRoutingSuccess(polylineOptions, result);
            }
        }
    }

    @Override
    protected void onCancelled() {
        for (RoutingListener listener : listeners) {
            listener.onRoutingCancelled();
        }
    }

    protected TravelMode getTravelMode() {
        return travelMode;
    }

    protected void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
    }

    protected UnitOption getUnitOption() {
        return unitOption;
    }

    protected void setUnitOption(UnitOption unitOption) {
        this.unitOption = unitOption;
    }

    protected LatLng getOrigin() {
        return origin;
    }

    protected void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    protected LatLng getDestination() {
        return destination;
    }

    protected void setDestination(LatLng destination) {
        this.destination = destination;
    }

    protected List<LatLng> getWaypoints() {
        return waypoints;
    }

    protected void setWaypoints(List<LatLng> waypoints) {
        this.waypoints = waypoints;
    }

    protected void setListeners(Set<RoutingListener> listeners) {
        this.listeners = listeners;
    }

    public static abstract class Builder<T extends Builder> {
        private TravelMode travelMode;
        private UnitOption unitOption;
        private LatLng origin;
        private LatLng destination;
        private final List<LatLng> waypoints = new ArrayList<>();
        private final Set<RoutingListener> listeners = new HashSet<>();

        public T travelMode(TravelMode travelMode) {
            this.travelMode = travelMode;
            return (T) this;
        }

        public T unitOption(UnitOption unitOption) {
            this.unitOption = unitOption;
            return (T) this;
        }

        public T origin(LatLng origin) {
            this.origin = origin;
            return (T) this;
        }

        public T destination(LatLng destination) {
            this.destination = destination;
            return (T) this;
        }

        public T waypoints(LatLng... points) {
            waypoints.clear();
            Collections.addAll(waypoints, points);
            return (T) this;
        }

        public T waypoints(List<LatLng> points) {
            waypoints.clear();
            waypoints.addAll(points);
            return (T) this;
        }

        public T addListener(RoutingListener listener) {
            listeners.add(listener);
            return (T) this;
        }

    }
}
