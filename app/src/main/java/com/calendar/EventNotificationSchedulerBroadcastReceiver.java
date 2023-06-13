package com.calendar;

import static android.content.ContentValues.TAG;
import static com.calendar.NotificationPublisher.scheduleNotificationForEventsFromDatabase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.calendar.activity.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Broadcast Receiver which set notifications for events from database on device booted event
 */
public class EventNotificationSchedulerBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    static final String MY_PACKAGE_REPLACED = "android.intent.action.MY_PACKAGE_REPLACED";

    /**
     * Method automatically invoke by system
     * @param context application context
     * @param intent intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (remindersShouldBeSet(intent)) {

            Log.d("Events from database",
                "Set up event reminders from database on " + intent.getAction() + " event.");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                                                .getReference("Calendar");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    scheduleNotificationForEventsFromDatabase(dataSnapshot, context);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "calendarClicked - onCancelled", error.toException());
                }
            });
        }
    }

    private boolean remindersShouldBeSet(Intent intent) {
        return intent.getAction().equals(ACTION_BOOT_COMPLETED) ||
                intent.getAction().equals(MY_PACKAGE_REPLACED) ||
                intent.getAction().equals(MainActivity.BROADCAST);
    }
}
