package com.sensis.voyager.app.routing;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Leg {
    private final List<Step> steps = new ArrayList<>();
    private LatLng startLocation;
    private LatLng endLocation;
    private String startAddress;
    private String endAddress;
    private String durationText;
    private String distanceText;
    private double duration;
    private double distance;

    public List<Step> getSteps() {
        return steps;
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public void setDurationValue(double duration) {
        this.duration = duration;
    }

    public double getDurationValue() {
        return duration;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public void setDistanceValue(double distance) {
        this.distance = distance;
    }

    public double getDistanceValue() {
        return distance;
    }
}
