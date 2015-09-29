package com.sensis.voyager.app.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.android.gms.location.places.Place;
import com.sensis.voyager.app.R;
import com.sensis.voyager.app.db.RouteContract;
import com.sensis.voyager.app.db.WaypointContract;
import com.sensis.voyager.app.preferences.TravelMode;
import com.sensis.voyager.app.preferences.UnitOption;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Table(name = RouteContract.TABLE, id = RouteContract.Columns.ID)
public final class RouteModel extends BaseModel {

    public static final Parcelable.Creator<RouteModel> CREATOR = new Parcelable.Creator<RouteModel>() {
        public RouteModel createFromParcel(Parcel in) {
            return RouteModel.load(RouteModel.class, in.readLong());
        }

        public RouteModel[] newArray(int size) {
            return new RouteModel[size];
        }
    };

    @Column(name = RouteContract.Columns.NAME)
    private String name;

    @Column(name = RouteContract.Columns.TRAVEL_MODE)
    private TravelMode travelMode;

    @Column(name = RouteContract.Columns.UNIT_OPTION)
    private UnitOption unitOption;

    @Column(name = RouteContract.Columns.LAST_OPTIMAL_CALCULATION_DATE)
    private Date lastOptimalCalculationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserFriendlyName(Context context) {
        if (name == null || name.isEmpty()) {
            String createdDateString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(getCreatedDate());
            return String.format(context.getString(R.string.created_at), createdDateString);
        }

        return name;
    }

    public TravelMode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
    }

    public UnitOption getUnitOption() {
        return unitOption;
    }

    public void setUnitOption(UnitOption unitOption) {
        this.unitOption = unitOption;
    }

    public boolean isRoundTrip() {
        return !new Select().from(WaypointModel.class)
                            .where(WaypointContract.Columns.ROUTE + " = ?", getId())
                            .and(WaypointContract.Columns.ORDER + " = ?", WaypointModel.END_WAYPOINT_ORDER)
                            .exists();
    }

    public void setStart(Place place) {
        if (getId() == null) {
            // Route must have an Id before continuing
            saveWithAudit();
        }

        WaypointModel startWaypoint = getStart();
        if (startWaypoint == null) {
            // No start waypoint exists yet
            startWaypoint = new WaypointModel();
            startWaypoint.setRoute(this);
            startWaypoint.setOrder(WaypointModel.START_WAYPOINT_ORDER);
        }

        startWaypoint.setPlace(PlaceModel.fromPlace(place));
        startWaypoint.saveWithAudit();
    }

    public void setEnd(Place place) {
        if (getId() == null) {
            // Route must have an Id before continuing
            saveWithAudit();
        }

        WaypointModel endWaypoint = getEnd();
        if (isRoundTrip()) {
            if (place != null) {
                // Switch from round trip to non-round trip
                endWaypoint = new WaypointModel();
                endWaypoint.setRoute(this);
                endWaypoint.setOrder(WaypointModel.END_WAYPOINT_ORDER);
            }
        } else {
            if (place == null) {
                // Switch from non-round trip to round trip
                if (endWaypoint != null) {
                    endWaypoint.delete();
                    endWaypoint = null;
                }
            }
        }

        if (endWaypoint != null) {
            endWaypoint.setPlace(PlaceModel.fromPlace(place));
            endWaypoint.saveWithAudit();
        }

        saveWithAudit();
    }

    public WaypointModel addWaypoint(Place place, String nickname) {
        if (getId() == null) {
            // Route must have an Id before continuing
            saveWithAudit();
        }

        WaypointModel waypoint = new WaypointModel();
        waypoint.setRoute(this);
        waypoint.setNickname(nickname);
        waypoint.setOrder(getNumberOfWaypoints());
        waypoint.setPlace(PlaceModel.fromPlace(place));
        waypoint.saveWithAudit();

        return waypoint;
    }

    public void deleteWaypointAt(int order) {
        if (order == WaypointModel.START_WAYPOINT_ORDER || order == WaypointModel.END_WAYPOINT_ORDER) {
            // Do not allow deleting the start or end point
            throw new IllegalArgumentException("Not allowed to delete the start or end point");
        }

        new Delete().from(WaypointModel.class)
                    .where(WaypointContract.Columns.ROUTE + " = ?", getId())
                    .and(WaypointContract.Columns.ORDER + " = ?", order)
                    .execute();

        new Update(WaypointModel.class)
                .set(WaypointContract.Columns.ORDER + " = " + WaypointContract.Columns.ORDER + " - 1")
                .where(String.format("%s = ? AND %s > ? AND %s <> ?",
                                WaypointContract.Columns.ROUTE, WaypointContract.Columns.ORDER, WaypointContract.Columns.ORDER),
                        getId(), order, WaypointModel.END_WAYPOINT_ORDER)
                .execute();
    }

    public List<WaypointModel> getWaypoints() {
        return new Select().from(WaypointModel.class)
                           .where(WaypointContract.Columns.ROUTE + " = ?", getId())
                           .and(WaypointContract.Columns.ORDER + " NOT IN (?, ?)", WaypointModel.START_WAYPOINT_ORDER, WaypointModel.END_WAYPOINT_ORDER)
                           .orderBy(WaypointContract.Columns.ORDER)
                           .execute();
    }

    public WaypointModel getStart() {
        return new Select().from(WaypointModel.class)
                           .where(WaypointContract.Columns.ROUTE + " = ?", getId())
                           .and(WaypointContract.Columns.ORDER + " = ?", WaypointModel.START_WAYPOINT_ORDER)
                           .executeSingle();
    }

    public WaypointModel getEnd() {
        return new Select().from(WaypointModel.class)
                           .where(WaypointContract.Columns.ROUTE + " = ?", getId())
                           .and(WaypointContract.Columns.ORDER + " = ?", WaypointModel.END_WAYPOINT_ORDER)
                           .executeSingle();
    }

    public int getNumberOfWaypoints() {
        return new Select().from(WaypointModel.class)
                           .where(WaypointContract.Columns.ROUTE + " = ?", getId())
                           .and(WaypointContract.Columns.ORDER + " NOT IN (?, ?)", WaypointModel.START_WAYPOINT_ORDER, WaypointModel.END_WAYPOINT_ORDER)
                           .count();
    }

    public WaypointModel getWaypointAt(int order) {
        return new Select().from(WaypointModel.class)
                           .where(WaypointContract.Columns.ROUTE + " = ?", getId())
                           .and(WaypointContract.Columns.ORDER + " = ?", order)
                           .executeSingle();
    }

    public List<WaypointModel> getWaypoints(int startOrder, int endOrder) {
        if (startOrder > endOrder) {
            throw new IllegalArgumentException("startOrder cannot be greater than endOrder");
        }

        return new Select().from(WaypointModel.class)
                           .where(WaypointContract.Columns.ROUTE + " = ?", getId())
                           .and(WaypointContract.Columns.ORDER + " >= ?", startOrder)
                           .and(WaypointContract.Columns.ORDER + " <= ?", endOrder)
                           .orderBy(WaypointContract.Columns.ORDER)
                           .execute();
    }

    public String getUserFriendlyLastOptimalCalculationDate(Context context) {
        if (lastOptimalCalculationDate == null) {
            return context.getString(R.string.not_calculated_yet);
        }

        String dateString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(lastOptimalCalculationDate);
        return String.format(context.getString(R.string.calculated_at), dateString);
    }

    public Date getLastOptimalCalculationDate() {
        return lastOptimalCalculationDate;
    }

    public void setLastOptimalCalculationDate(Date lastOptimalCalculationDate) {
        this.lastOptimalCalculationDate = lastOptimalCalculationDate;
    }

    /**
     * Checks whether the ordering of the waypoints were overridden manually.
     */
    public boolean isWaypointReorderedManually() {
        if (lastOptimalCalculationDate == null) {
            return false;
        }

        // Check if there is any waypoint whose last updated date is later than the automatic calculation date
        return new Select().from(WaypointModel.class)
                           .where(WaypointContract.Columns.ROUTE + " = ?", getId())
                           .and(WaypointContract.Columns.LAST_UPDATED_DATE + " > ?", lastOptimalCalculationDate.getTime())
                           .exists();
    }

    public String getUserFriendlyWaypointManualReorderDate(Context context) {
        if (!isWaypointReorderedManually()) {
            return null;
        }

        WaypointModel mostRecentUpdatedWaypoint = new Select().from(WaypointModel.class)
                                                              .where(WaypointContract.Columns.ROUTE + " = ?", getId())
                                                              .and(WaypointContract.Columns.LAST_UPDATED_DATE + " > ?", lastOptimalCalculationDate.getTime())
                                                              .orderBy(WaypointContract.Columns.LAST_UPDATED_DATE + " DESC")
                                                              .limit(1)
                                                              .executeSingle();

        String dateString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(mostRecentUpdatedWaypoint.getLastUpdatedDate());
        return String.format(context.getString(R.string.manually_reordered_at), dateString);
    }
}
