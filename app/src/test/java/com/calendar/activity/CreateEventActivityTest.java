package com.calendar.activity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.calendar.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CreateEventActivityTest {

    @Test
    public void shouldDeleteOldEvent_isCorrect() {
        CreateEventActivity createEventActivity = new CreateEventActivity();
        Event oldEvent = new Event("test", "2023-05-18", false);
        Event newEvent = new Event("test", "2023-05-18", false);
        boolean actualOutput = createEventActivity.shouldDeleteOldEvent(oldEvent, newEvent);

        assertFalse(actualOutput);
    }

    @Test
    public void shouldDeleteOldEvent_isNotCorrect() {
        CreateEventActivity createEventActivity = new CreateEventActivity();
        Event oldEvent = new Event("test", "2023-05-19", false);
        Event newEvent = new Event("test", "2023-05-18", false);
        boolean actualOutput = createEventActivity.shouldDeleteOldEvent(oldEvent, newEvent);

        assertTrue(actualOutput);
    }
}