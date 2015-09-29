package com.sensis.voyager.app.routeview;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.activeandroid.ActiveAndroid;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sensis.voyager.app.ExtraData;
import com.sensis.voyager.app.R;
import com.sensis.voyager.app.ioc.BaseFragment;
import com.sensis.voyager.app.model.RouteModel;
import com.sensis.voyager.app.model.WaypointModel;
import com.sensis.voyager.app.preferences.TravelMode;
import com.sensis.voyager.app.preferences.UnitOption;
import com.sensis.voyager.app.preferences.VoyagerPreferences;
import com.sensis.voyager.app.routing.Route;
import com.sensis.voyager.app.routing.RoutingListener;
import com.sensis.voyager.app.routing.router.BruteForceRouter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RouteDetailsFragment extends BaseFragment implements RoutingListener, FragmentDataChangedListener {

    @Bind(R.id.idHolder)
    protected TextView idTextView;

    @Bind(R.id.route_name)
    protected TextView routeNameTextView;

    @Bind(R.id.start_address)
    protected TextView startAddressTextView;

    @Bind(R.id.end_address)
    protected TextView endAddressTextView;

    @Bind(R.id.num_stops)
    protected TextView numberOfStopsTextView;

    @Bind(R.id.optimal_route_list)
    protected RecyclerView recyclerView;

    @Bind(R.id.optimal_route_bottom_divider)
    protected View optimalRouteBottomDividerView;

    @Bind(R.id.last_optimal_calculated_date)
    protected TextView lastOptimalCalculatedDateTextView;

    @Bind(R.id.manual_reorder_date)
    protected TextView manualReorderDateTextView;

    private ShowWaypointOnMapListener showWaypointOnMapListener;
    private MaterialDialog progressDialog;
    private RouteModel route;
    private OptimalRouteRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_route_details;
    }

    @Override
    public void onDataChanged() {
        setRouteDetails(route);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        route = getArguments().getParcelable(ExtraData.EXTRA_ROUTE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        recyclerViewAdapter = new OptimalRouteRecyclerViewAdapter(getActivity(), route, showWaypointOnMapListener);
        recyclerView.setAdapter(recyclerViewAdapter);

        setRouteDetails(route);
    }

    @Override
    public void onRoutingFailure() {
        Log.d("Routing", "Routing failed");

        progressDialog.dismiss();
        Toast.makeText(getActivity(), R.string.error_route_calculate_fail, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingStart() {
        Log.d("Routing", "Routing started");

        progressDialog = new MaterialDialog.Builder(getActivity())
                .content(R.string.fetching_route_info)
                .progress(true, 0)
                .cancelable(false)
                .build();
        progressDialog.show();
    }

    @Override
    public void onRoutingSuccess(PolylineOptions polylineOptions, Route computedRoute) {
        Log.d("Routing", "Routing succeeded");

        progressDialog.dismiss();

        List<WaypointModel> waypoints = route.getWaypoints();
        int[] newWaypointOrder = computedRoute.getWaypointOrder();

        ActiveAndroid.beginTransaction();
        try {
            // SQLite checks the constraints after every row update and not at the end of the transaction.
            // So reset the order first so that we don't fail due to a unique constraint
            for (WaypointModel waypoint : waypoints) {
                waypoint.setOrder(-1);
                waypoint.saveWithAudit();
            }

            for (int i = 0; i < newWaypointOrder.length; i++) {
                WaypointModel waypoint = waypoints.get(newWaypointOrder[i]);
                waypoint.setOrder(i);
                waypoint.saveWithAudit();
            }

            route.setLastOptimalCalculationDate(new Date());
            route.saveWithAudit();

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

        onDataChanged();
    }

    @Override
    public void onRoutingCancelled() {
        Log.d("Routing", "Routing cancelled");

        progressDialog.dismiss();
        Toast.makeText(getActivity(), R.string.route_calculation_cancelled, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.calculate_optimal_btn)
    public void onOptimizeRouteButtonClick(View view) {
        if (!ConnectionUtils.isOnline(getActivity())) {
            Toast.makeText(getActivity(), R.string.error_no_internet_connection, Toast.LENGTH_LONG).show();
            return;
        }

        RouteModel route = getArguments().getParcelable(ExtraData.EXTRA_ROUTE);

        List<WaypointModel> waypoints = route.getWaypoints();
        if (waypoints.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_insufficient_stops, Toast.LENGTH_LONG).show();
        } else {
            LatLng origin = route.getStart().getLatLng();
            LatLng destination = route.isRoundTrip() ? origin : route.getEnd().getLatLng();

            List<LatLng> waypointsList = new ArrayList<>(waypoints.size());
            for (WaypointModel waypoint : waypoints) {
                waypointsList.add(waypoint.getLatLng());
            }

            VoyagerPreferences preferences = new VoyagerPreferences(getActivity().getApplicationContext());
            TravelMode travelMode = route.getTravelMode();
            if (travelMode == null) {
                // Fallback to default travel mode preferences
                travelMode = preferences.getTravelMode();
            }

            UnitOption unitOption = route.getUnitOption();
            if (unitOption == null) {
                // Fallback to default unit preferences
                unitOption = preferences.getDistanceUnit();
            }

            //new GoogleDirectionsRouter.Builder()
            new BruteForceRouter.Builder()
                    .travelMode(travelMode)
                    .unitOption(unitOption)
                    .origin(origin)
                    .destination(destination)
                    .waypoints(waypointsList)
                    .addListener(this)
                    .build()
                    .execute();
        }
    }

    void setShowWaypointOnMapListener(ShowWaypointOnMapListener showWaypointOnMapListener) {
        this.showWaypointOnMapListener = showWaypointOnMapListener;
    }

    private void setRouteDetails(RouteModel route) {
        // Set route details card info
        idTextView.setText(String.valueOf(route.getId()));
        routeNameTextView.setText(route.getUserFriendlyName(getActivity()));
        startAddressTextView.setText(route.getStart().getPlace().getAddress());
        setEndAddress(route);
        numberOfStopsTextView.setText(String.valueOf(route.getNumberOfWaypoints()));
        lastOptimalCalculatedDateTextView.setText(route.getUserFriendlyLastOptimalCalculationDate(getActivity()));
        setManualReorderDate(route);

        // Set optimal route card info
        if (route.getLastOptimalCalculationDate() == null) {
            optimalRouteBottomDividerView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            optimalRouteBottomDividerView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void setEndAddress(RouteModel route) {
        if (route.isRoundTrip()) {
            endAddressTextView.setText(R.string.end_point_same_as_start);
            endAddressTextView.setTypeface(endAddressTextView.getTypeface(), Typeface.ITALIC);
        } else {
            endAddressTextView.setText(route.getEnd().getPlace().getAddress());
        }
    }

    private void setManualReorderDate(RouteModel route) {
        String text = route.getUserFriendlyWaypointManualReorderDate(getActivity());
        if (text == null || text.isEmpty()) {
            manualReorderDateTextView.setVisibility(View.GONE);
        } else {
            manualReorderDateTextView.setVisibility(View.VISIBLE);
            manualReorderDateTextView.setText(text);
        }
    }
}
