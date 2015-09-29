package com.sensis.voyager.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.activeandroid.Cache;
import com.activeandroid.query.Select;
import com.sensis.voyager.app.db.RouteContract;
import com.sensis.voyager.app.ioc.BaseFragment;
import com.sensis.voyager.app.model.RouteModel;
import com.sensis.voyager.app.routeview.CreateOrEditRouteActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import butterknife.Bind;
import butterknife.OnClick;


public class RouteListFragment extends BaseFragment {

    @Bind(R.id.rvRoutes)
    @Nullable
    protected RecyclerView recyclerView;

    private Cursor cursor;

    @Override
    protected int getLayoutId() {
        String getAllRoutesSql = new Select().from(RouteModel.class)
                                             .orderBy(RouteContract.Columns.LAST_UPDATED_DATE + " DESC")
                                             .toSql();
        cursor = Cache.openDatabase().rawQuery(getAllRoutesSql, null);

        if (cursor.getCount() > 0) {
            return R.layout.fragment_route_list;
        } else {
            return R.layout.empty_view;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (cursor.getCount() > 0 && recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            HorizontalDividerItemDecoration itemDecoration = new HorizontalDividerItemDecoration.Builder(getActivity())
                    .showLastDivider()
                    .build();
            recyclerView.addItemDecoration(itemDecoration);

            RouteRecyclerViewAdapter adapter = new RouteRecyclerViewAdapter(getActivity(), cursor);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        if (cursor != null) {
            cursor.close();
        }

        super.onDestroy();
    }

    @OnClick(R.id.add_route_btn)
    public void onAddRouteButtonClick(View view) {
        Intent intent = new Intent(getActivity(), CreateOrEditRouteActivity.class);
        startActivity(intent);
    }
}
