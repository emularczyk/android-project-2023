package com.calendar;

import static com.calendar.CalendarUtils.convertDateStringToRegardlessOfTheYear;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EventRepository {

    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReference("Calendar");

    public static void saveEventList(List<Event> holidayList) {
        for (Event holiday : holidayList) {
            saveEvent(holiday);
        }
    }

    public static void saveEvent(Event event) {
        databaseReference.child(event.getDate().toString())
                .child(event.getTitle())
                .child("description")
                .setValue(event.getNote());
    }

    public static void saveEventListRegardlessOfTheYear(List<Event> eventList) {
        for (Event holiday : eventList) {
            saveEventRegardlessOfTheYear(holiday);
        }
    }

    public static void saveEventRegardlessOfTheYear(Event event) {
        String dateRegardlessOfYear;
        dateRegardlessOfYear = convertDateStringToRegardlessOfTheYear(event.getDate().toString());
        databaseReference.child(dateRegardlessOfYear)
                .child(event.getTitle())
                .child("description")
                .setValue(event.getNote());
    }
}
