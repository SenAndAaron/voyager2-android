package com.sensis.voyager.app.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.android.gms.maps.model.LatLng;
import com.sensis.voyager.app.db.WaypointContract;

/**
 * A waypoint relates a Route with a Place, in a specific order.
 */
@Table(name = WaypointContract.TABLE, id = WaypointContract.Columns.ID)
public class WaypointModel extends BaseModel {

    public static final Parcelable.Creator<WaypointModel> CREATOR = new Parcelable.Creator<WaypointModel>() {
        public WaypointModel createFromParcel(Parcel in) {
            return WaypointModel.load(WaypointModel.class, in.readLong());
        }

        public WaypointModel[] newArray(int size) {
            return new WaypointModel[size];
        }
    };

    static final int START_WAYPOINT_ORDER = Integer.MIN_VALUE;
    static final int END_WAYPOINT_ORDER = Integer.MAX_VALUE;

    private static final String ROUTE_ORDER_GROUP = "_route_order_group";

    @Column(name = WaypointContract.Columns.ORDER, notNull = true, index = true, uniqueGroups = {ROUTE_ORDER_GROUP})
    private int order;

    @Column(name = WaypointContract.Columns.ROUTE, notNull = true, onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE, uniqueGroups = {ROUTE_ORDER_GROUP})
    private RouteModel route;

    @Column(name = WaypointContract.Columns.PLACE, notNull = true, onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private PlaceModel place;

    @Column(name = WaypointContract.Columns.NICKNAME)
    private String nickname;

    public LatLng getLatLng() {
        return place.getLatLng();
    }

    public PlaceModel getPlace() {
        return place;
    }

    void setPlace(PlaceModel place) {
        this.place = place;
    }

    public RouteModel getRoute() {
        return route;
    }

    void setRoute(RouteModel route) {
        this.route = route;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getNickname() {
        return nickname;
    }

    void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
