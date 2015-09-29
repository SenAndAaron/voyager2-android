package com.sensis.voyager.app.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public final class VoyagerPreferences {
    private static final String ALLOW_LANDSCAPE = "allowLandscape";
    private static final boolean DEFAULT_ALLOW_LANDSCAPE = false;

    private static final String DISTANCE_UNIT = "distanceUnit";
    private static final UnitOption DEFAULT_DISTANCE_UNIT = UnitOption.DEFAULT;

    private static final String TRAVEL_MODE = "travelMode";
    private static final TravelMode DEFAULT_TRAVEL_MODE = TravelMode.DRIVING;

    private static final String ALLOW_HIGHWAYS = "allowHighways";
    private static final boolean DEFAULT_ALLOW_HIGHWAYS = true;

    private static final String ALLOW_TOLLS = "AllowTolls";
    private static final boolean DEFAULT_ALLOW_TOLLS = false;

    private SharedPreferences preferences;

    public VoyagerPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setAllowLandscape(boolean allowLandscape) {
        preferences.edit().putBoolean(ALLOW_LANDSCAPE, allowLandscape).apply();
    }

    public boolean getAllowLandscape() {
        return preferences.getBoolean(ALLOW_LANDSCAPE, DEFAULT_ALLOW_LANDSCAPE);
    }

    public void setDistanceUnit(UnitOption option) {
        preferences.edit().putInt(DISTANCE_UNIT, option.getValue()).apply();
    }

    public UnitOption getDistanceUnit() {
        return UnitOption.fromInteger(preferences.getInt(DISTANCE_UNIT, DEFAULT_DISTANCE_UNIT.getValue()));
    }

    public void setTravelMode(TravelMode mode) {
        preferences.edit().putInt(TRAVEL_MODE, mode.getValue()).apply();
    }

    public TravelMode getTravelMode() {
        return TravelMode.fromInteger(preferences.getInt(TRAVEL_MODE, DEFAULT_TRAVEL_MODE.getValue()));
    }

    public void setAllowHighways(boolean allowHighways) {
        preferences.edit().putBoolean(ALLOW_HIGHWAYS, allowHighways).apply();
    }

    public boolean getAllowHighways() {
        return preferences.getBoolean(ALLOW_HIGHWAYS, DEFAULT_ALLOW_HIGHWAYS);
    }

    public void setAllowTolls(boolean allowTolls) {
        preferences.edit().putBoolean(ALLOW_TOLLS, allowTolls).apply();
    }

    public boolean getAllowTolls() {
        return preferences.getBoolean(ALLOW_TOLLS, DEFAULT_ALLOW_TOLLS);
    }
}
