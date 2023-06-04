package com.calendar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.calendar.activity.CreateEventActivity;
import com.calendar.activity.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AddEventTests {

    @Test
    public void checkAddEventIsDisplayed() {
        ActivityScenario activityScenario = ActivityScenario.launch(CreateEventActivity.class);

        onView(withId(R.id.addEvent)).check(matches(isDisplayed()));
    }

    @Test
    public void checkAddEventView() {
        ActivityScenario activityScenario = ActivityScenario.launch(CreateEventActivity.class);

        onView(withId(R.id.title))
                .check(matches(withHint(R.string.enter_title)));
        onView(withId(R.id.dateText))
                .check(matches(withText(R.string.set_date)));
       onView(withId(R.id.note))
                .check(matches(withHint(R.string.enter_note)));
        onView(withId(R.id.advancedSettings))
                .check(matches(withText(R.string.show_all_settings)));
        onView(withId(R.id.saveEventButton))
                .check(matches(withText(R.string.save_event)));
    }

    @Test
    public void checkAddEventWithAdvancedSettingsView() {
        ActivityScenario activityScenario = ActivityScenario.launch(CreateEventActivity.class);

        onView(withId(R.id.advancedSettings))
                .perform(click())
                .check(matches(withText(R.string.show_all_settings)));
        onView(withId(R.id.setReminder))
                .perform(click())
                .check(matches(withText(R.string.ReminderText)));

        onView(withId(R.id.title))
                .check(matches(withHint(R.string.enter_title)));
        onView(withId(R.id.dateText))
                .check(matches(withText(R.string.set_date)));
        onView(withId(R.id.note))
                .check(matches(withHint(R.string.enter_note)));
        onView(withId(R.id.saveEventButton))
                .check(matches(withText(R.string.save_event)));
        onView(withId(R.id.annual))
                .check(matches(withText(R.string.annualText)));
        onView(withId(R.id.free))
                .check(matches(withText(R.string.freeText)));
        onView(withId(R.id.reminderTimeCheckBox))
                .check(matches(withText(R.string.set_reminder_time)));
    }
}