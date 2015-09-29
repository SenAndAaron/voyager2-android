package com.sensis.voyager.app.routeview;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.activeandroid.ActiveAndroid;
import com.sensis.voyager.app.model.RouteModel;
import com.sensis.voyager.app.model.WaypointModel;

import java.util.List;

public class WaypointsItemTouchHelperCallBack extends ItemTouchHelper.SimpleCallback {
    private final WaypointsRecyclerViewAdapter recyclerViewAdapter;
    private final RouteModel route;

    public WaypointsItemTouchHelperCallBack(WaypointsRecyclerViewAdapter recyclerViewAdapter, RouteModel route) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        this.recyclerViewAdapter = recyclerViewAdapter;
        this.route = route;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        int startOrder = Math.min(fromPosition, toPosition);
        int endOrder = Math.max(fromPosition, toPosition);
        List<WaypointModel> waypoints = route.getWaypoints(startOrder, endOrder);

        // Rearrange the order of the waypoints
        ActiveAndroid.beginTransaction();
        try {
            // SQLite checks the constraints after every row update and not at the end of the transaction.
            // So reset the order first so that we don't fail due to a unique constraint
            for (WaypointModel waypoint : waypoints) {
                waypoint.setOrder(-1);
                waypoint.saveWithAudit();
            }

            if (fromPosition < toPosition) {
                // Moved down
                WaypointModel waypoint = waypoints.get(0);
                waypoint.setOrder(toPosition);
                waypoint.saveWithAudit();

                for (int i = fromPosition + 1; i <= toPosition; i++) {
                    waypoint = waypoints.get(i - fromPosition);
                    waypoint.setOrder(i - 1);
                    waypoint.saveWithAudit();
                }
            } else {
                // Moved up
                WaypointModel waypoint = waypoints.get(waypoints.size() - 1);
                waypoint.setOrder(toPosition);
                waypoint.saveWithAudit();

                for (int i = toPosition; i < fromPosition; i++) {
                    waypoint = waypoints.get(i - toPosition);
                    waypoint.setOrder(i + 1);
                    waypoint.saveWithAudit();
                }
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

        recyclerViewAdapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Do nothing
    }
}
