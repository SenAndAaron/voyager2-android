package com.sensis.voyager.app.ioc;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

/**
 * Provides a base class with inversion of control available for View objects.
 * Reduces boilerplate code such as:
 * <code>
 * TextView blah = (TextView) view.findViewById(R.id.blah)
 * </code>
 */
public abstract class BaseCursorAdapter extends CursorAdapter {

    public BaseCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    protected abstract int getLayoutId();

    /**
     * The newView method is used to inflate a new view and return it,
     * you don't bind any data to the view at this point.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(getLayoutId(), parent, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
