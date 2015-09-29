package com.sensis.voyager.app;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class InstrumentationTestHelper {

    public static void testPlacesAutoCompleteShows(int autoCompleteViewId) {
        onView(withId(autoCompleteViewId))
                .perform(typeText("pearson airport"));

        // Now explicitly tap on a completion.
        onView(withText(containsString("Toronto Pearson International Airport")))
                .inRoot(isPlatformPopup())
                .check(matches(isDisplayed()))
                .perform(click());

        // And by clicking on the auto complete term, the text should be filled in.
        onView(withId(autoCompleteViewId))
                .check(matches(withText(containsString("Toronto Pearson International Airport"))));
    }

    public static <T extends Activity> void checkToastMessage(ActivityTestRule<T> activityTestRule, int resourceId) {
        onView(withText(resourceId))
                .inRoot(withDecorView(not(activityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}
