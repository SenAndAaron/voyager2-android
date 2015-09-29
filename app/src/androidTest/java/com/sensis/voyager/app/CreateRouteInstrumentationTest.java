package com.sensis.voyager.app;

import android.support.test.rule.ActivityTestRule;
import android.view.KeyEvent;

import com.sensis.voyager.app.routeview.CreateOrEditRouteActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.sensis.voyager.app.InstrumentationTestHelper.checkToastMessage;
import static com.sensis.voyager.app.InstrumentationTestHelper.testPlacesAutoCompleteShows;
import static org.hamcrest.Matchers.startsWith;

public class CreateRouteInstrumentationTest extends BaseInstrumentationTest {

    // Specify the activity to be launched before each test
    @Rule
    public ActivityTestRule<CreateOrEditRouteActivity> activityTestRule = new ActivityTestRule<>(CreateOrEditRouteActivity.class);

    /**
     * Attempting to create a new route without specifying a start point should fail.
     */
    @Test
    public void testNoStartPointSaveFails() {
        onView(withId(R.id.start_point))
                .perform(clearText());

        onView(withId(R.id.setup_macroSavebtn))
                .perform(click());

        // Check that an error toast is shown when the save button is clicked without entering in a start point
        checkToastMessage(activityTestRule, R.string.error_start_point_invalid);
    }

    @Test
    public void testInvalidStartPointSaveFails() {
        testPlacesAutoCompleteShows(R.id.start_point);

        onView(withId(R.id.start_point))
                .perform(doubleClick())
                .perform(pressKey(KeyEvent.KEYCODE_DEL));

        // Check that a warning icon is shown when an address was not selected from the dropdown
        onView(withId(R.id.start_point_warn_btn))
                .check(matches(isDisplayed()));

        onView(withId(R.id.setup_macroSavebtn))
                .perform(click());

        // Check that an error toast is shown when the save button is clicked without selecting an address from the dropdown
        checkToastMessage(activityTestRule, R.string.error_start_point_invalid);
    }

    /**
     * Creating a new route without specifying an end point should create a round-trip route.
     */
    @Test
    public void testNoEndPointIsRoundTrip() {
        testPlacesAutoCompleteShows(R.id.start_point);

        onView(withId(R.id.setup_macroSavebtn))
                .perform(click());

        // Check route details
        onView(withId(R.id.end_address))
                .check(matches(withText(R.string.end_point_same_as_start)));
        onView(withId(R.id.num_stops))
                .check(matches(withText("0")));
    }

    /**
     * Make sure places autocomplete works for end point.
     */
    @Test
    public void testEndPointAutoCompleteShows() {
        testPlacesAutoCompleteShows(R.id.end_point);
    }

    /**
     * When a route is created without a name, it should display "Created at {DATETIME}".
     */
    @Test
    public void testNoNameShowsCreationDate() {
        testPlacesAutoCompleteShows(R.id.start_point);

        onView(withId(R.id.setup_macroSavebtn))
                .perform(click());

        String routeNamePrefix = String.format(activityTestRule.getActivity().getString(R.string.created_at), "");
        onView(withId(R.id.route_name))
                .check(matches(withText(startsWith(routeNamePrefix))));
    }
}