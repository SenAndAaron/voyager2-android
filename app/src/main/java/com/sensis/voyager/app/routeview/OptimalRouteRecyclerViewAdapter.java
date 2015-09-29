package com.sensis.voyager.app.routeview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.sensis.voyager.app.R;
import com.sensis.voyager.app.model.RouteModel;
import com.sensis.voyager.app.model.WaypointModel;

public class OptimalRouteRecyclerViewAdapter extends RecyclerView.Adapter<OptimalRouteRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final RouteModel route;
    private final ShowWaypointOnMapListener showWaypointOnMapListener;

    public OptimalRouteRecyclerViewAdapter(Context context, RouteModel route, ShowWaypointOnMapListener showWaypointOnMapListener) {
        this.context = context;
        this.route = route;
        this.showWaypointOnMapListener = showWaypointOnMapListener;
    }

    @Override
    public int getItemCount() {
        // Need to include start and end points
        return route.getNumberOfWaypoints() + 2;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WaypointModel waypoint;
        if (position == 0) {
            // Start point
            waypoint = route.getStart();
        } else if (position == getItemCount() - 1) {
            // End point
            if (route.isRoundTrip()) {
                waypoint = route.getStart();
            } else {
                waypoint = route.getEnd();
            }
        } else {
            waypoint = route.getWaypointAt(position - 1);
        }

        holder.idTextView.setText(String.valueOf(waypoint.getId()));
        holder.orderTextView.setText(String.valueOf(position + 1));

        String address = waypoint.getPlace().getName();
        if (address == null || address.isEmpty()) {
            address = waypoint.getPlace().getAddress();
        }
        holder.addressTextView.setText(address);

        String nickname = waypoint.getNickname();
        if (nickname != null && !nickname.isEmpty()) {
            nickname += " @ ";
        }
        holder.nicknameTextView.setText(nickname);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.one_optimal_route_stop_row_view, parent, false);
        return new ViewHolder(itemView, showWaypointOnMapListener);
    }

    /**
     * Provide a direct reference to each of the views within a data item
     * used to cache the views within the item layout for fast access
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.idHolder)
        protected TextView idTextView;

        @Bind(R.id.order)
        protected TextView orderTextView;

        @Bind(R.id.nickname)
        protected TextView nicknameTextView;

        @Bind(R.id.address)
        protected TextView addressTextView;

        private final ShowWaypointOnMapListener showWaypointOnMapListener;

        public ViewHolder(View itemView, ShowWaypointOnMapListener showWaypointOnMapListener) {
            super(itemView);
            this.showWaypointOnMapListener = showWaypointOnMapListener;
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            WaypointModel waypoint = WaypointModel.load(WaypointModel.class, Long.parseLong(idTextView.getText().toString()));
            showWaypointOnMapListener.showWaypointOnMap(waypoint);
        }
    }
}
