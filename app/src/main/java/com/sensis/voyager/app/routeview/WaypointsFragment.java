package com.sensis.voyager.app.routeview;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import butterknife.Bind;
import butterknife.OnClick;
import com.activeandroid.ActiveAndroid;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.sensis.voyager.app.ExtraData;
import com.sensis.voyager.app.R;
import com.sensis.voyager.app.ioc.BaseFragment;
import com.sensis.voyager.app.model.RouteModel;
import com.sensis.voyager.app.model.WaypointModel;
import com.sensis.voyager.app.touch.DragListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

public class WaypointsFragment extends BaseFragment implements FragmentDataChangedListener, DragListener {

    @Bind(R.id.rvWaypoints)
    protected RecyclerView recyclerView;

    @Bind(R.id.emptyWaypointsMessageTextView)
    protected TextView emptyWaypointsMessageTextView;

    private ShowWaypointOnMapListener showWaypointOnMapListener;
    private WaypointsRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.AdapterDataObserver recyclerViewAdapterDataObserver;
    private ItemTouchHelper itemTouchHelper;
    private RouteModel route;
    private Place waypointPlace;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_waypoints;
    }

    @Override
    public void onDataChanged() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        HorizontalDividerItemDecoration itemDecoration = new HorizontalDividerItemDecoration.Builder(getActivity())
                .showLastDivider()
                .build();
        recyclerView.addItemDecoration(itemDecoration);

        route = getArguments().getParcelable(ExtraData.EXTRA_ROUTE);
        recyclerViewAdapter = new WaypointsRecyclerViewAdapter(getActivity(), route, this, showWaypointOnMapListener);
        recyclerViewAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                setEmptyWaypointsMessageVisibility();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                setEmptyWaypointsMessageVisibility();
            }

            @Override
            public void onChanged() {
                setEmptyWaypointsMessageVisibility();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                setEmptyWaypointsMessageVisibility();
            }
        };
        recyclerViewAdapter.registerAdapterDataObserver(recyclerViewAdapterDataObserver);
        recyclerView.setAdapter(recyclerViewAdapter);

        itemTouchHelper = new ItemTouchHelper(new WaypointsItemTouchHelperCallBack(recyclerViewAdapter, route));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        setEmptyWaypointsMessageVisibility();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerViewAdapter.unregisterAdapterDataObserver(recyclerViewAdapterDataObserver);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @OnClick(R.id.add_waypoint_btn)
    public void onAddWaypointButtonClick(View view) {
        waypointPlace = null;
        MaterialDialog addWaypointDialog = new MaterialDialog.Builder(getActivity())
                .autoDismiss(false)
                .positiveText(R.string.action_save)
                .negativeText(R.string.action_cancel)
                .customView(R.layout.dialog_add_waypoint, true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (waypointPlace == null) {
                            Toast.makeText(getActivity(), R.string.error_place_autocomplete_invalid, Toast.LENGTH_LONG).show();
                        } else {
                            WaypointModel waypoint;
                            ActiveAndroid.beginTransaction();
                            try {
                                String nickname = ((EditText) dialog.findViewById(R.id.nickname)).getText().toString().trim();
                                waypoint = route.addWaypoint(waypointPlace, nickname);

                                ActiveAndroid.setTransactionSuccessful();
                            } finally {
                                ActiveAndroid.endTransaction();
                            }

                            recyclerViewAdapter.notifyItemInserted(waypoint.getOrder());
                            dialog.dismiss();
                        }
                    }
                })
                .cancelable(false)
                .build();
        addWaypointDialog.show();

        // Set the waypoint based on the value selected from the autocomplete text view.
        // TODO: Break dependency with GoogleApiClientActivity
        GoogleApiClientActivity activity = (GoogleApiClientActivity) getActivity();
        PlaceAutoCompleteAdapter autoCompleteAdapter = activity.getPlaceAutoCompleteAdapter();

        final ImageButton waypointAddressWarningButton = (ImageButton) addWaypointDialog.findViewById(R.id.waypoint_address_warn_btn);
        waypointAddressWarningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.error_place_autocomplete_invalid, Toast.LENGTH_LONG).show();
            }
        });

        AutoCompleteTextView waypointAddressAutoComplete = (AutoCompleteTextView) addWaypointDialog.findViewById(R.id.waypoint_address);
        waypointAddressAutoComplete.setAdapter(autoCompleteAdapter);
        waypointAddressAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                // This text watcher sets the waypoint to null, because once there's a change
                // after a value has been selected from the dropdown, the value has to reselected
                // from dropdown to get the correct location.
                waypointPlace = null;

                if (s.length() > 0) {
                    waypointAddressWarningButton.setVisibility(View.VISIBLE);
                } else {
                    waypointAddressWarningButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
        waypointAddressAutoComplete.setOnItemClickListener(new PlaceAutoCompleteOnItemClickListener(
                activity.getGoogleApiClient(),
                autoCompleteAdapter,
                new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e("Waypoint", "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                        } else {
                            // Get the Place object from the buffer.
                            waypointPlace = places.get(0);
                            waypointAddressWarningButton.setVisibility(View.GONE);
                        }
                    }
                }));
    }

    void setShowWaypointOnMapListener(ShowWaypointOnMapListener showWaypointOnMapListener) {
        this.showWaypointOnMapListener = showWaypointOnMapListener;
    }

    private void setEmptyWaypointsMessageVisibility() {
        if (route.getNumberOfWaypoints() == 0) {
            emptyWaypointsMessageTextView.setVisibility(View.VISIBLE);
        } else {
            emptyWaypointsMessageTextView.setVisibility(View.INVISIBLE);
        }
    }
}
