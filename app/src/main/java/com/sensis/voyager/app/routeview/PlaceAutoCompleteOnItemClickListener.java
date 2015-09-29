package com.sensis.voyager.app.routeview;

import android.view.View;
import android.widget.AdapterView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

public class PlaceAutoCompleteOnItemClickListener implements AdapterView.OnItemClickListener {
    private final GoogleApiClient googleApiClient;
    private final PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private final ResultCallback<PlaceBuffer> resultCallback;

    public PlaceAutoCompleteOnItemClickListener(GoogleApiClient googleApiClient, PlaceAutoCompleteAdapter adapter, ResultCallback<PlaceBuffer> resultCallback) {
        this.googleApiClient = googleApiClient;
        this.placeAutoCompleteAdapter = adapter;
        this.resultCallback = resultCallback;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Sets the start point based on the values selected from the autocomplete text view
        PlaceAutoCompleteAdapter.PlaceAutocomplete item = placeAutoCompleteAdapter.getItem(position);

        // Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
        Places.GeoDataApi.getPlaceById(googleApiClient, item.getPlaceId())
                         .setResultCallback(resultCallback);
    }
}