package com.sensis.voyager.app.routeview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import butterknife.Bind;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sensis.voyager.app.ExtraData;
import com.sensis.voyager.app.MainActivity;
import com.sensis.voyager.app.R;
import com.sensis.voyager.app.model.RouteModel;
import com.sensis.voyager.app.model.WaypointModel;

import java.util.HashMap;
import java.util.Map;


public class RouteActivity extends GoogleApiClientActivity implements ShowWaypointOnMapListener {

    private static final int[] tabTitles = {R.string.tab_details, R.string.tab_stops, R.string.tab_map};

    private final Map<Integer, Fragment> pageReferenceMap = new HashMap<>();

    @Bind(R.id.viewpager)
    protected ViewPager viewPager;

    @Bind(R.id.sliding_tabs)
    protected TabLayout tabLayout;

    private RouteModel route;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_route;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        route = getIntent().getParcelableExtra(ExtraData.EXTRA_ROUTE);
        setTitle(route.getUserFriendlyName(this));

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return tabTitles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getString(tabTitles[position]);
            }

            @Override
            public Fragment getItem(int position) {
                Fragment fragment;
                switch (tabTitles[position]) {
                    case R.string.tab_details:
                        RouteDetailsFragment routeDetailsFragment = new RouteDetailsFragment();
                        routeDetailsFragment.setShowWaypointOnMapListener(RouteActivity.this);
                        fragment = routeDetailsFragment;
                        break;

                    case R.string.tab_stops:
                        WaypointsFragment waypointsFragment = new WaypointsFragment();
                        waypointsFragment.setShowWaypointOnMapListener(RouteActivity.this);
                        fragment = waypointsFragment;
                        break;

                    case R.string.tab_map:
                        fragment = new VoyagerMapFragment();
                        break;

                    default:
                        throw new IllegalArgumentException("Invalid position: " + position);
                }

                fragment.setArguments(getIntent().getExtras());
                return fragment;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Fragment item = (Fragment) super.instantiateItem(container, position);
                pageReferenceMap.put(position, item);
                return item;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
                pageReferenceMap.remove(position);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (pageReferenceMap.containsKey(position)) {
                    Object item = pageReferenceMap.get(position);
                    if (item instanceof FragmentDataChangedListener) {
                        ((FragmentDataChangedListener) item).onDataChanged();
                    }
                }
            }
        });

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_edit:
                Intent intent = new Intent(this, CreateOrEditRouteActivity.class);
                intent.putExtra(ExtraData.EXTRA_ROUTE, route);
                startActivity(intent);
                return true;

            case R.id.menu_delete:
                new MaterialDialog.Builder(this)
                        .title(R.string.confirm_delete_route_title)
                        .content(R.string.confirm_delete_route_content)
                        .positiveText(R.string.action_yes)
                        .negativeText(R.string.action_no)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                route.delete();

                                Intent intent = new Intent(RouteActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .cancelable(false)
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showWaypointOnMap(WaypointModel waypoint) {
        for (int i = 0; i < tabTitles.length; i++) {
            if (tabTitles[i] == R.string.tab_map) {
                if (pageReferenceMap.containsKey(i)) {
                    pageReferenceMap.get(i).getArguments().putParcelable(ExtraData.EXTRA_CENTER_ON_WAYPOINT, waypoint);
                } else {
                    getIntent().putExtra(ExtraData.EXTRA_CENTER_ON_WAYPOINT, waypoint);
                }

                viewPager.setCurrentItem(i, true);
                getIntent().removeExtra(ExtraData.EXTRA_CENTER_ON_WAYPOINT);
                break;
            }
        }
    }
}
