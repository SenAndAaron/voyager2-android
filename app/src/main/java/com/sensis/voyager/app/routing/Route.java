package com.sensis.voyager.app.routing;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private final List<LatLng> points = new ArrayList<>();
    private List<Leg> legs = new ArrayList<>();
    private String copyright;
    private String warning;
    private LatLngBounds latLgnBounds;
    private int[] waypointOrder;

    public void addPoint(LatLng point) {
        points.add(point);
    }

    public void addPoints(List<LatLng> points) {
        this.points.addAll(points);
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void addLeg(Leg leg) {
        legs.add(leg);
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getWarning() {
        return warning;
    }

    public LatLngBounds getLatLgnBounds() {
        return latLgnBounds;
    }

    public void setLatLgnBounds(LatLng northeast, LatLng southwest) {
        this.latLgnBounds = new LatLngBounds.Builder()
                .include(northeast)
                .include(southwest)
                .build();
    }

    public int[] getWaypointOrder() {
        return waypointOrder;
    }

    public void setWaypointOrder(int[] waypointOrder) {
        this.waypointOrder = waypointOrder;
    }
}
