package com.sensis.voyager.app.db;

public final class RouteContract {
    public static final String TABLE = "routes";

    public static class Columns extends BaseContract.Columns {
        public static final String NAME = "_name";
        public static final String TRAVEL_MODE = "_travel_mode";
        public static final String UNIT_OPTION = "_unit_option";
        public static final String LAST_OPTIMAL_CALCULATION_DATE = "_last_optimal_calculation_date";
    }
}
