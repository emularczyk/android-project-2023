package com.calendar;

import static com.calendar.CalendarUtils.convertDateStringToRegardlessOfTheYear;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Class with database operations for events
 */
public class EventRepository {

    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReference("Calendar");

    /**
     * Saves event list to database
     * @param holidayList a list of events to save
     */
    public static void saveEventList(List<Event> holidayList) {
        for (Event holiday : holidayList) {
            saveEvent(holiday);
        }
    }

    /**
     * Saves a single event to database
     * @param event event to save
     */
    public static void saveEvent(Event event) {
        if (event.isAnnual()) {
            saveAnnualEvent(event);
        } else {
            saveSingularEvent(event);
        }
    }

    private static void saveSingularEvent(Event event) {
        DatabaseReference eventReference = databaseReference.child(event.getDate())
                .child(event.getId());
        setEventValues(eventReference, event);
    }

    /**
     * Saves annual event to database
     * @param eventList annual event to save
     */
    public static void saveEventListRegardlessOfTheYear(List<Event> eventList) {
        for (Event holiday : eventList) {
            saveAnnualEvent(holiday);
        }
    }

    private static void saveAnnualEvent(Event event) {
        String dateRegardlessOfYear;
        dateRegardlessOfYear = convertDateStringToRegardlessOfTheYear(event.getDate());
        DatabaseReference eventReference = databaseReference.child(dateRegardlessOfYear)
                .child(event.getId());
        setEventValues(eventReference, event);
    }

    private static void setEventValues(DatabaseReference eventReference, Event event) {
        eventReference.child("title").setValue(event.getTitle());
        eventReference.child("isSystemEvent").setValue(event.isSystemEvent());
        eventReference.child("note").setValue(event.getNote());
        eventReference.child("isFree").setValue(event.isFreeFromWork());
        eventReference.child("isReminderOn").setValue(event.isReminderOn());
        eventReference.child("reminderTime").setValue(event.getReminderTime());
    }
}
