package com.sensis.voyager.app.menufragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnClick;

import com.sensis.voyager.app.MainActivity;
import com.sensis.voyager.app.R;
import com.sensis.voyager.app.ioc.BaseFragment;
import com.sensis.voyager.app.preferences.TravelMode;
import com.sensis.voyager.app.preferences.UnitOption;
import com.sensis.voyager.app.preferences.VoyagerPreferences;


public class AppSettingsFragment extends BaseFragment {

    @Bind(R.id.travel_mode_radio_group)
    protected RadioGroup travelModeRadioGroup;

    @Bind(R.id.distance_unit_radio_group)
    protected RadioGroup distanceUnitRadioGroup;

    @Bind(R.id.enableLandscape)
    protected Switch enableLandscapeSwitch;

    @Bind(R.id.allowHighways)
    protected Switch allowHighwaysSwitch;

    @Bind(R.id.allowTolls)
    protected Switch allowTollsSwitch;

    @Override
    protected int getLayoutId() {
        return R.layout.app_settings_view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        VoyagerPreferences preferences = new VoyagerPreferences(getActivity().getApplicationContext());

        ((RadioButton) travelModeRadioGroup.getChildAt(preferences.getTravelMode().getValue())).setChecked(true);
        ((RadioButton) distanceUnitRadioGroup.getChildAt(preferences.getDistanceUnit().getValue())).setChecked(true);
        enableLandscapeSwitch.setChecked(preferences.getAllowLandscape());
        allowHighwaysSwitch.setChecked(preferences.getAllowHighways());
        allowTollsSwitch.setChecked(preferences.getAllowTolls());
    }

    @OnClick(R.id.setup_macroSavebtn)
    public void onSaveButtonClick(View view) {
        VoyagerPreferences preferences = new VoyagerPreferences(getActivity().getApplicationContext());

        int index = travelModeRadioGroup.indexOfChild(getActivity().findViewById(travelModeRadioGroup.getCheckedRadioButtonId()));
        preferences.setTravelMode(TravelMode.fromInteger(index));

        index = distanceUnitRadioGroup.indexOfChild(getActivity().findViewById(distanceUnitRadioGroup.getCheckedRadioButtonId()));
        preferences.setDistanceUnit(UnitOption.fromInteger(index));

        preferences.setAllowLandscape(enableLandscapeSwitch.isChecked());
        preferences.setAllowHighways(allowHighwaysSwitch.isChecked());
        preferences.setAllowTolls(allowTollsSwitch.isChecked());

        navigateToLandingPage();
        Toast.makeText(getActivity().getApplicationContext(), R.string.app_settings_saved, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.setup_macroCancelbtn)
    public void onCancelButtonClick(View view) {
        navigateToLandingPage();
    }

    private void navigateToLandingPage() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
