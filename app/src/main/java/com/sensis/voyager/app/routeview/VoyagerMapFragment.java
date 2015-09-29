package com.sensis.voyager.app.routeview;

import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.sensis.voyager.app.ExtraData;
import com.sensis.voyager.app.model.RouteModel;
import com.sensis.voyager.app.model.WaypointModel;

import java.util.List;

public class VoyagerMapFragment extends SupportMapFragment implements FragmentDataChangedListener {

    private GoogleMap map;

    @Override
    public void onDataChanged() {
        resetMap();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMyLocationEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                resetMap();
            }
        });
    }

    private void resetMap() {
        if (map == null) {
            // Map not loaded yet. resetMap() will automatically be called once the map is loaded.
            return;
        }

        map.clear();

        RouteModel route = getArguments().getParcelable(ExtraData.EXTRA_ROUTE);

        LatLng start = route.getStart().getLatLng();

        LatLng cameraLocation = start;
        WaypointModel waypoint = getArguments().getParcelable(ExtraData.EXTRA_CENTER_ON_WAYPOINT);
        if (waypoint != null) {
            cameraLocation = waypoint.getLatLng();
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLocation, 14));

        IconGenerator iconFactory = new IconGenerator(getActivity());
        iconFactory.setStyle(IconGenerator.STYLE_GREEN);
        map.addMarker(new MarkerOptions()
                .position(start)
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("1"))));

        iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
        List<WaypointModel> waypoints = route.getWaypoints();
        int numberOfWaypoints = waypoints.size();
        for (int i = 0; i < numberOfWaypoints; i++) {
            map.addMarker(new MarkerOptions()
                    .position(waypoints.get(i).getLatLng())
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(String.valueOf(i + 2)))));
        }

        // TODO: Should we show a marker if the end is the same as start?
        if (!route.isRoundTrip()) {
            iconFactory.setStyle(IconGenerator.STYLE_RED);
            map.addMarker(new MarkerOptions()
                    .position(route.getEnd().getLatLng())
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(String.valueOf(numberOfWaypoints + 2)))));
        }
    }
}
