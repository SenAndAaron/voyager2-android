package com.sensis.voyager.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sensis.voyager.app.ioc.BaseActivity;
import com.sensis.voyager.app.menufragments.AboutFragment;
import com.sensis.voyager.app.menufragments.InfoFragment;

import butterknife.Bind;

public class MainActivity extends BaseActivity {
    private MaterialDialog progressDialog;

    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawer;

    @Bind(R.id.nvView)
    protected NavigationView nvDrawer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the menu icon instead of the launcher icon.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        // Setup drawer view
        setupDrawerContent(nvDrawer);
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.launching_app_msg)
                .progress(true, 0)
                .cancelable(false)
                .build();
        progressDialog.show();
        setDefaultLandingPage();
        progressDialog.dismiss();
    }

    private void setDefaultLandingPage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, getRouteListFragment()).commit();
    }

    private Fragment getRouteListFragment() {
        return new RouteListFragment();
    }

    private Fragment getHelpFragment() {
        return new InfoFragment();
    }

    private Fragment getAboutFragment() {
        return new AboutFragment();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment;

        switch (menuItem.getItemId()) {
            case R.id.nav_routes:
                fragment = getRouteListFragment();
                break;
            case R.id.nav_help:
                fragment = getHelpFragment();
                break;
            case R.id.nav_about:
                fragment = getAboutFragment();
                break;
            default:
                fragment = getRouteListFragment();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title(R.string.exit_confirm)
                .positiveText(R.string.action_yes)
                .neutralText(R.string.action_no)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .cancelable(false)
                .show();
    }
}
