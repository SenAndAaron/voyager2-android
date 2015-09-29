package com.sensis.voyager.app.routing;

import com.google.android.gms.maps.model.LatLng;

public class Step {
    private LatLng start;
    private LatLng end;
    private String instruction;

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public LatLng getStart() {
        return start;
    }

    public LatLng getEnd() {
        return end;
    }

    public void setEnd(LatLng end) {
        this.end = end;
    }
}