package com.sensis.voyager.app.routeview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sensis.voyager.app.R;
import com.sensis.voyager.app.model.RouteModel;
import com.sensis.voyager.app.model.WaypointModel;
import com.sensis.voyager.app.touch.DragListener;

public class WaypointsRecyclerViewAdapter extends RecyclerView.Adapter<WaypointsRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private RouteModel route;
    private final DragListener dragStartListener;
    private final ShowWaypointOnMapListener showWaypointOnMapListener;

    public WaypointsRecyclerViewAdapter(Context context, RouteModel route, DragListener dragStartListener, ShowWaypointOnMapListener showWaypointOnMapListener) {
        this.context = context;
        this.route = route;
        this.dragStartListener = dragStartListener;
        this.showWaypointOnMapListener = showWaypointOnMapListener;
    }

    @Override
    public int getItemCount() {
        return route.getNumberOfWaypoints();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        WaypointModel waypoint = route.getWaypointAt(position);

        holder.idTextView.setText(String.valueOf(waypoint.getId()));
        holder.addressTextView.setText(waypoint.getPlace().getAddress());

        String nickname = waypoint.getNickname();
        if (nickname != null && !nickname.isEmpty()) {
            nickname += " @ ";
        }
        holder.nicknameTextView.setText(nickname);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(context)
                        .title(R.string.confirm_delete_stop_title)
                        .content(R.string.confirm_delete_stop_content)
                        .positiveText(R.string.action_yes)
                        .negativeText(R.string.action_no)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                route.deleteWaypointAt(position);
                                notifyItemRemoved(position);
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        });

        holder.reorderButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.one_waypoint_row_view, parent, false);
        return new ViewHolder(itemView, showWaypointOnMapListener);
    }

    /**
     * Provide a direct reference to each of the views within a data item
     * used to cache the views within the item layout for fast access
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.idHolder)
        protected TextView idTextView;

        @Bind(R.id.nickname)
        protected TextView nicknameTextView;

        @Bind(R.id.address)
        protected TextView addressTextView;

        @Bind(R.id.delete_btn)
        protected ImageButton deleteButton;

        @Bind(R.id.reorder_btn)
        protected ImageButton reorderButton;

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
