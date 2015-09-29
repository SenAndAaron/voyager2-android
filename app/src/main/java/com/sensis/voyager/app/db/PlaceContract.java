package com.sensis.voyager.app.db;

public class PlaceContract {
    public static final String TABLE = "places";

    public static class Columns extends BaseContract.Columns {
        public static final String GOOGLE_ID = "_google_id";
        public static final String ADDRESS = "_address";
        public static final String LATITUDE = "_latitude";
        public static final String LONGITUDE = "_longitude";
        public static final String NAME = "_name";
    }
}
