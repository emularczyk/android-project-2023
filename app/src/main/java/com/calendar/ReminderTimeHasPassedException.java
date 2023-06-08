package com.calendar;

/**
 * Exception thrown when reminder time is older than the date it reminds
 */
public class ReminderTimeHasPassedException extends RuntimeException {

    /**
     * @param eventId id of event which cause the exception
     */
    public ReminderTimeHasPassedException(final String eventId) {
        super("Reminder time for event with id `" + eventId + " shouldn't be before current date when is annual.");
    }
}
