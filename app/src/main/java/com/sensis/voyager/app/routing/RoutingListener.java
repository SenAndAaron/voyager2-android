package com.sensis.voyager.app.routing;

import com.google.android.gms.maps.model.PolylineOptions;

public interface RoutingListener {
    void onRoutingFailure();

    void onRoutingStart();

    void onRoutingSuccess(PolylineOptions polylineOptions, Route route);

    void onRoutingCancelled();
}
