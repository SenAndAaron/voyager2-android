package com.sensis.voyager.app;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.sensis.voyager.app.db.RouteContract;
import com.sensis.voyager.app.model.RouteModel;
import org.junit.After;
import org.junit.Before;

import java.util.Date;

public abstract class BaseInstrumentationTest {
    private Date testStart;

    @Before
    public void setUp() {
        testStart = new Date();
    }

    @After
    public void tearDown() {
        new Delete().from(RouteModel.class)
                    .where(RouteContract.Columns.CREATED_DATE + " >= ?", testStart.getTime())
                    .execute();
        ActiveAndroid.clearCache();
    }
}
