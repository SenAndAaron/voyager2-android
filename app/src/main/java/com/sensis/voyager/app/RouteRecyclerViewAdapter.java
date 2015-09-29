package com.sensis.voyager.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.sensis.voyager.app.model.RouteModel;
import com.sensis.voyager.app.routeview.RouteActivity;
import com.sensis.voyager.app.routeview.RouteCursorAdapter;

/**
 * Create the basic adapter extending from RecyclerView.Adapter
 * Note that we specify the custom ViewHolder which gives us access to our views
 */
public class RouteRecyclerViewAdapter extends RecyclerView.Adapter<RouteRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final CursorAdapter cursorAdapter;

    public RouteRecyclerViewAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursorAdapter = new RouteCursorAdapter(context, cursor);
    }

    @Override
    public int getItemCount() {
        return cursorAdapter.getCount();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Pass the binding operation to cursor loader
        cursorAdapter.getCursor().moveToPosition(position);
        cursorAdapter.bindView(holder.itemView, context, cursorAdapter.getCursor());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Pass the inflater job to the cursor adapter
        View view = cursorAdapter.newView(context, cursorAdapter.getCursor(), parent);
        return new ViewHolder(context, view);
    }

    /**
     * Provide a direct reference to each of the views within a data item
     * used to cache the views within the item layout for fast access
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.idHolder)
        protected TextView idTextView;

        private final Context context;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            long routeId = Long.parseLong(idTextView.getText().toString());
            RouteModel route = RouteModel.load(RouteModel.class, routeId);

            Intent intent = new Intent(context, RouteActivity.class);
            intent.putExtra(ExtraData.EXTRA_ROUTE, route);
            context.startActivity(intent);
        }
    }
}