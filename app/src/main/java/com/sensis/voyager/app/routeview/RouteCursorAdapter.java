package com.sensis.voyager.app.routeview;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;

import com.sensis.voyager.app.R;
import com.sensis.voyager.app.db.RouteContract;
import com.sensis.voyager.app.ioc.BaseCursorAdapter;
import com.sensis.voyager.app.model.RouteModel;

public class RouteCursorAdapter extends BaseCursorAdapter {
    @Bind(R.id.idHolder)
    protected TextView idTextView;

    @Bind(R.id.route_name)
    protected TextView routeNameTextView;

    @Bind(R.id.start_address)
    protected TextView startAddressTextView;

    @Bind(R.id.end_address)
    protected TextView endAddressTextView;

    public RouteCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.one_route_row_view;
    }

    /**
     * The bindView method is used to bind all data to a given view
     * such as setting the text on a TextView.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long routeId = cursor.getLong(cursor.getColumnIndexOrThrow(RouteContract.Columns.ID));
        RouteModel route = RouteModel.load(RouteModel.class, routeId);

        idTextView.setText(String.valueOf(route.getId()));
        routeNameTextView.setText(route.getUserFriendlyName(context));
        startAddressTextView.setText(route.getStart().getPlace().getAddress());
        setEndAddress(route);
    }

    private void setEndAddress(RouteModel route) {
        if (route.isRoundTrip()) {
            endAddressTextView.setText(R.string.end_point_same_as_start);
            endAddressTextView.setTypeface(endAddressTextView.getTypeface(), Typeface.ITALIC);
        } else {
            endAddressTextView.setText(route.getEnd().getPlace().getAddress());
        }
    }
}