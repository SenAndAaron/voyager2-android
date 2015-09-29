package com.sensis.voyager.app.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.sensis.voyager.app.db.PlaceContract;

import java.util.Date;

public abstract class BaseModel extends Model implements Parcelable {
    @Column(name = PlaceContract.Columns.CREATED_DATE, notNull = true)
    private Date createdDate;

    @Column(name = PlaceContract.Columns.LAST_UPDATED_DATE, notNull = true)
    private Date lastUpdatedDate;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(getId());
    }

    /**
     * Saves this entity, including any audit trails.
     * Workaround for ActiveAndroid's save() not having any hooks.
     */
    public void saveWithAudit() {
        if (createdDate == null) {
            createdDate = new Date();
        }

        lastUpdatedDate = new Date();
        save();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }
}
