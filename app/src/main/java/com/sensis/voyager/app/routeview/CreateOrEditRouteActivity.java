package com.sensis.voyager.app.routeview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.activeandroid.ActiveAndroid;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sensis.voyager.app.ExtraData;
import com.sensis.voyager.app.R;
import com.sensis.voyager.app.model.PlaceModel;
import com.sensis.voyager.app.model.RouteModel;

import java.util.List;
import java.util.Locale;

public class CreateOrEditRouteActivity extends GoogleApiClientActivity {

    @Bind(R.id.start_point)
    protected AutoCompleteTextView startPointAutoComplete;

    @Bind(R.id.start_point_warn_btn)
    protected ImageButton startPointWarningButton;

    @Bind(R.id.end_point)
    protected AutoCompleteTextView endPointAutoComplete;

    @Bind(R.id.end_point_warn_btn)
    protected ImageButton endPointWarningButton;

    @Bind(R.id.route_name)
    protected EditText routeNameEditText;

    private RouteModel route;
    private Place startPlace;
    private Place endPlace;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_edit_route;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        route = getIntent().getParcelableExtra(ExtraData.EXTRA_ROUTE);
        if (route == null) {
            setTitle(R.string.title_activity_create_route);

            route = new RouteModel();
        } else {
            setTitle(R.string.title_activity_edit_route);

            startPlace = new MockPlace(route.getStart().getPlace());
            startPointAutoComplete.setText(startPlace.getAddress());

            if (route.isRoundTrip()) {
                endPlace = null;
                endPointAutoComplete.setText(null);
            } else {
                endPlace = new MockPlace(route.getEnd().getPlace());
                endPointAutoComplete.setText(endPlace.getAddress());
            }

            routeNameEditText.setText(route.getName());
        }

        // Set the start and end points based on the values selected from the autocomplete text views.
        startPointAutoComplete.setAdapter(getPlaceAutoCompleteAdapter());
        startPointAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                // This text watcher sets the start point to null, because once there's a change
                // after a value has been selected from the dropdown, the value has to reselected
                // from dropdown to get the correct location.
                startPlace = null;

                if (s.length() > 0) {
                    startPointWarningButton.setVisibility(View.VISIBLE);
                } else {
                    startPointWarningButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }


        });
        startPointAutoComplete.setOnItemClickListener(new PlaceAutoCompleteOnItemClickListener(
                getGoogleApiClient(),
                getPlaceAutoCompleteAdapter(),
                new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e("Start Point", "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                        } else {
                            // Get the Place object from the buffer.
                            startPlace = places.get(0);
                            startPointWarningButton.setVisibility(View.GONE);

                            EditText yourEditText = (EditText) findViewById(R.id.start_point);
                            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(yourEditText.getWindowToken(), 0);
                        }
                    }
                }));

        endPointAutoComplete.setAdapter(getPlaceAutoCompleteAdapter());
        endPointAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                // This text watcher sets the end point to null, because once there's a change
                // after a value has been selected from the dropdown, the value has to reselected
                // from dropdown to get the correct location.
                endPlace = null;

                if (s.length() > 0) {
                    endPointWarningButton.setVisibility(View.VISIBLE);
                } else {
                    endPointWarningButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
        endPointAutoComplete.setOnItemClickListener(new PlaceAutoCompleteOnItemClickListener(
                getGoogleApiClient(),
                getPlaceAutoCompleteAdapter(),
                new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e("End Point", "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                        } else {
                            // Get the Place object from the buffer.
                            endPlace = places.get(0);
                            endPointWarningButton.setVisibility(View.GONE);

                            EditText yourEditText = (EditText) findViewById(R.id.end_point);
                            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(yourEditText.getWindowToken(), 0);
                        }
                    }
                }));
        routeNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    EditText yourEditText = (EditText) findViewById(R.id.end_point);
                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(yourEditText.getWindowToken(), 0);
                }
            }
        });
    }

    @OnClick({R.id.start_point_warn_btn, R.id.end_point_warn_btn})
    public void onPlacesAutoCompleteWarningButtonClick(View view) {
        Toast.makeText(this, R.string.error_place_autocomplete_invalid, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.setup_macroSavebtn)
    public void onSaveButtonClick(View view) {
        if (startPlace == null) {
            Toast.makeText(this, R.string.error_start_point_invalid, Toast.LENGTH_LONG).show();
        } else {
            ActiveAndroid.beginTransaction();
            try {
                route.setName(routeNameEditText.getText().toString().trim());
                route.setStart(startPlace);
                route.setEnd(endPlace);

                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }

            Intent routeActivityIntent = new Intent(this, RouteActivity.class);
            routeActivityIntent.putExtra(ExtraData.EXTRA_ROUTE, route);
            startActivity(routeActivityIntent);
            Toast.makeText(getApplicationContext(), R.string.route_saved, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.setup_macroCancelbtn)
    public void onCancelButtonClick(View view) {
        finish();
    }

    /**
     * Mock representation of a Place.
     */
    private class MockPlace implements Place {

        private final String googleID;
        private final String address;
        private final LatLng latLng;

        public MockPlace(PlaceModel model) {
            googleID = model.getGoogleID();
            address = model.getAddress();
            latLng = model.getLatLng();
        }

        @Override
        public String getId() {
            return googleID;
        }

        @Override
        public List<Integer> getPlaceTypes() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public CharSequence getAddress() {
            return address;
        }

        @Override
        public Locale getLocale() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public CharSequence getName() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public LatLng getLatLng() {
            return latLng;
        }

        @Override
        public LatLngBounds getViewport() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public Uri getWebsiteUri() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public CharSequence getPhoneNumber() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public float getRating() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public int getPriceLevel() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public Place freeze() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public boolean isDataValid() {
            throw new RuntimeException("Not implemented");
        }
    }
}
