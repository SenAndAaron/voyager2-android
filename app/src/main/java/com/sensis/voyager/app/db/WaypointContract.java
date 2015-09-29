package com.sensis.voyager.app.db;

public class WaypointContract {
    public static final String TABLE = "getWaypoints";

    public static class Columns extends BaseContract.Columns {
        public static final String ROUTE = "_route";
        public static final String PLACE = "_place";
        public static final String ORDER = "_order";
        public static final String NICKNAME = "_nickname";
    }
}
