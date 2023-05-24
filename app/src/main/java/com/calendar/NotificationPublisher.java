package com.calendar;

import static android.content.Context.ALARM_SERVICE;

import static com.calendar.CalendarUtils.getReminderTime;
import static java.lang.Boolean.parseBoolean;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public class NotificationPublisher extends BroadcastReceiver {

    public static final LocalTime DEFAULT_REMINDER_TIME = LocalTime.NOON.minusHours(2);
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelName = "Notification about event";
        NotificationChannel chan = new NotificationChannel("com.calendar",
                channelName, NotificationManager.IMPORTANCE_LOW);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(chan);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
        Log.d("Notification publisher", "Notification has been published");
    }

    public static void scheduleNotificationForEventsFromDatabase(@NonNull final DataSnapshot dataSnapshot,
                                                                 final Context context) {
        EventController eventController = new EventController(context);
        for (DataSnapshot snap : dataSnapshot.getChildren()) {
            String dateString = Objects.requireNonNull(snap.getKey());
            boolean isAnnual = dateString.contains("XXXX-");

            for (DataSnapshot snapshot : snap.getChildren()) {
                String id = snapshot.getKey();
                String title = Objects.requireNonNull(snapshot.child("title").getValue()).toString();
                String note = snapshot.child("note").exists() ? snapshot.child("note").getValue().toString() : "";
                boolean isSystemEvent = parseBoolean(Objects.requireNonNull(snapshot.child("isSystemEvent").getValue()).toString());
                boolean isFree = parseBoolean(Objects.requireNonNull(snapshot.child("isFree").getValue()).toString());
                boolean isReminderOn = parseBoolean(Objects.requireNonNull(snapshot.child("isReminderOn").getValue()).toString());
                LocalTime reminderTimer = snapshot.child("reminderTime").exists() ? getReminderTime(snapshot) : null;

                if (isReminderOn) {
                    Event event = new Event(
                            id,
                            title,
                            dateString,
                            note,
                            isSystemEvent,
                            isAnnual,
                            isFree,
                            isReminderOn,
                            reminderTimer);

                    try {
                        NotificationPublisher.scheduleNotification(event, context,
                                eventController.prepareEventNotification(event));
                        Log.i("EventReminderOnBootSet", title + " " + dateString);
                    } catch (ReminderTimeHasPassedException exception) {
                        Log.i("Couldn't save reminder", exception.getMessage());
                    }
                }
            }
        }
    }

    public static void scheduleNotification(final Event eventToNotify, final Context context,
                                            final Notification notification) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent notificationPublisherIntent = new Intent(context, NotificationPublisher.class);
        int newNotificationId = eventToNotify.getId()
                                           .hashCode();
        notificationPublisherIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, newNotificationId);
        notificationPublisherIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context,
                newNotificationId,
                notificationPublisherIntent,
                0);

        ZonedDateTime zonedNotificationDateTime = getZonedReminderDateTime(eventToNotify);
        if(eventToNotify.isAnnual()) {
            alarmManager.setRepeating(AlarmManager.RTC,
                                        zonedNotificationDateTime.toInstant()
                                                                 .toEpochMilli(),
                                        DateUtils.YEAR_IN_MILLIS,
                                        notificationPendingIntent);
            Log.d("Annual alarm", "Annual alarm set at " + zonedNotificationDateTime);
        } else {
            alarmManager.setExact(AlarmManager.RTC,
                                    zonedNotificationDateTime.toInstant()
                                                             .toEpochMilli(),
                                    notificationPendingIntent);
            Log.d("Alarm", "Alarm set at " + zonedNotificationDateTime);
        }
    }

    public static void unScheduleNotification(Event oldEvent, final Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent notificationPublisherIntent = new Intent(context, NotificationPublisher.class);
        int newNotificationId = oldEvent.getId()
                                        .hashCode();

        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context,
                newNotificationId,
                notificationPublisherIntent,
                0);

        alarmManager.cancel(notificationPendingIntent);
        Log.d("Alarm", "Alarm at " + oldEvent.getDate() + oldEvent.getReminderTime() + " cancelled");
    }

    private static ZonedDateTime getZonedReminderDateTime(Event eventToNotify) {
        LocalTime reminderTime = eventToNotify.getReminderTime();

        if(reminderTime == null) {
            reminderTime = DEFAULT_REMINDER_TIME;
        }

        String eventDate = eventToNotify.getDate();
        if (eventToNotify.isAnnual()) {
            eventDate = eventDate.replace("XXXX", CalendarUtils.yearFromDate(LocalDate.now()));
        }

        LocalDate reminderDate = LocalDate.parse(eventDate);
        if (reminderDate.isBefore(LocalDate.now())) {
            if (eventToNotify.isAnnual()) {
                reminderDate = reminderDate.plusYears(1);
            } else {
                throw new ReminderTimeHasPassedException(eventToNotify.getId());
            }
        }

        return LocalDateTime.of(reminderDate, reminderTime)
                            .atZone(ZoneId.systemDefault());
    }
}
