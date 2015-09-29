package com.sensis.voyager.app.routeview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.sensis.voyager.app.R;
import com.sensis.voyager.app.ioc.BaseActivity;

public abstract class GoogleApiClientActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Define a request code to send to Google Play services.
    // This code is returned in Activity.onActivityResult
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient googleApiClient;
    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        LatLng center;
        Location lastKnownLocation = ConnectionUtils.getLastKnownLocation(this);
        if (lastKnownLocation == null) {
            // Assume center of Toronto for bounds calculation to start with until a location is detected
            center = new LatLng(43.7001100, -79.4163000);
        } else {
            center = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        }
        setPlaceAutoCompleteAdapterBounds(center);
    }

    /**
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    /**
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    /**
     * Handle results returned to the activity by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        if (requestCode == CONNECTION_FAILURE_RESOLUTION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                // Try to connect again
                googleApiClient.connect();
            }
        }
    }

    /**
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        switch (i) {
            case CAUSE_SERVICE_DISCONNECTED:
                Toast.makeText(this, R.string.error_disconnected, Toast.LENGTH_LONG).show();
                break;

            case CAUSE_NETWORK_LOST:
                Toast.makeText(this, R.string.error_network_lost, Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Play services can resolve some errors it detects.
        // If the error has a resolution, try sending an Intent to start a Google Play services activity that can resolve error.
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.e("Connection Failed", "Google Play services canceled the original PendingIntent", e);
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_location_services_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called by Location Services when the request to connect the client finishes successfully.
     * At this point, you can request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
            setPlaceAutoCompleteAdapterBounds(center);
        }
    }

    protected final GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    protected final boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            errorDialog.show();
            return false;
        }

        // Google Play services is available
        return true;
    }

    protected final PlaceAutoCompleteAdapter getPlaceAutoCompleteAdapter() {
        return placeAutoCompleteAdapter;
    }

    private void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    private void setPlaceAutoCompleteAdapterBounds(LatLng center) {
        // Add 2 points 1000m northEast and southWest of the center.
        // They increase the bounds only, if they are not already larger than this.
        // 1000m on the diagonal translates into about 709m to each direction.
        LatLng northEast = SphericalUtil.computeOffset(center, 709, 45);
        LatLng southWest = SphericalUtil.computeOffset(center, 709, 225);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(northEast)
                .include(southWest)
                .build();

        if (placeAutoCompleteAdapter == null) {
            placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    googleApiClient,
                    bounds);
        }
    }
}
