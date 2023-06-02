package com.calendar.activity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.calendar.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CreateEventActivityTest {

    @Test
    public void shouldDeleteOldEvent_isFalse() {
        CreateEventActivity createEventActivity = new CreateEventActivity();
        Event oldEvent = new Event("test1", "2023-05-18", false);
        Event newEvent = new Event("test2", "2023-05-18", false);
        boolean actualOutput = createEventActivity.shouldDeleteOldEvent(oldEvent, newEvent);

        assertFalse(actualOutput);
    }

    @Test
    public void shouldDeleteOldEventDifferentDate_isTrue() {
        CreateEventActivity createEventActivity = new CreateEventActivity();
        Event oldEvent = new Event("test1", "2023-05-19", false);
        Event newEvent = new Event("test2", "2023-05-18", false);
        boolean actualOutput = createEventActivity.shouldDeleteOldEvent(oldEvent, newEvent);

        assertTrue(actualOutput);
    }

    @Test
    public void shouldDeleteOldEventDifferentType_isTrue() {
        CreateEventActivity createEventActivity = new CreateEventActivity();
        Event oldEvent = new Event(UUID.randomUUID().toString(),
                "test1", "2023-05-18", "", false,
                true, false, false, null);
        Event newEvent = new Event(UUID.randomUUID().toString(),
                "test1", "2023-05-18", "", false,
                false, false, false, null);
        boolean actualOutput = createEventActivity.shouldDeleteOldEvent(oldEvent, newEvent);

        assertTrue(actualOutput);
    }
}