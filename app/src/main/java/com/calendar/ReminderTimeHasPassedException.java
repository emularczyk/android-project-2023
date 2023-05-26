package com.calendar;

public class ReminderTimeHasPassedException extends RuntimeException {

    public ReminderTimeHasPassedException(final String eventId) {
        super("Reminder time for event with id `" + eventId + " shouldn't be before current date when is annual.");
    }
}
