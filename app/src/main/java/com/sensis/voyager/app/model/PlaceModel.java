package com.sensis.voyager.app.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.sensis.voyager.app.db.PlaceContract;

@Table(name = PlaceContract.TABLE, id = PlaceContract.Columns.ID)
public class PlaceModel extends BaseModel {

    public static final Parcelable.Creator<PlaceModel> CREATOR = new Parcelable.Creator<PlaceModel>() {
        public PlaceModel createFromParcel(Parcel in) {
            return PlaceModel.load(PlaceModel.class, in.readLong());
        }

        public PlaceModel[] newArray(int size) {
            return new PlaceModel[size];
        }
    };

    @Column(name = PlaceContract.Columns.GOOGLE_ID, unique = true, notNull = true)
    private String googleID;

    @Column(name = PlaceContract.Columns.ADDRESS, notNull = true)
    private String address;

    @Column(name = PlaceContract.Columns.LATITUDE, notNull = true)
    private double latitude;

    @Column(name = PlaceContract.Columns.LONGITUDE, notNull = true)
    private double longitude;

    @Column(name = PlaceContract.Columns.NAME)
    private String name;

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public String getGoogleID() {
        return googleID;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public static PlaceModel fromPlace(Place place) {
        PlaceModel placeModel = new Select().from(PlaceModel.class)
                                            .where(PlaceContract.Columns.GOOGLE_ID + " = ?", place.getId())
                                            .executeSingle();
        if (placeModel == null) {
            placeModel = new PlaceModel();
            placeModel.googleID = place.getId();
            placeModel.address = place.getAddress().toString();
            placeModel.latitude = place.getLatLng().latitude;
            placeModel.longitude = place.getLatLng().longitude;
            placeModel.name = place.getName().toString();
            placeModel.saveWithAudit();
        }

        return placeModel;
    }
}
