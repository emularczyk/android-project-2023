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

/**
 * Broadcast Receiver which prepares and set up alarm manager with notification
 */
public class NotificationPublisher extends BroadcastReceiver {

    public static final LocalTime DEFAULT_REMINDER_TIME = LocalTime.NOON.minusHours(2);
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";


    /**
     * Method automatically invoke by for example alarm manager
     * @param context application context
     * @param intent intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = prepareNotificationManager(context);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
        Log.d("Notification publisher", "Notification has been published");
    }

    /**
     * Method used to schedule notification for given events with alarm manager
     * @param dataSnapshot events from database
     * @param context application context
     */
    public static void scheduleNotificationForEventsFromDatabase(@NonNull final DataSnapshot dataSnapshot,
                                                                 final Context context) {
        for (DataSnapshot snap : dataSnapshot.getChildren()) {
            String dateString = Objects.requireNonNull(snap.getKey());
            boolean isAnnual = dateString.contains("XXXX-");

            for (DataSnapshot snapshot : snap.getChildren()) {
                Event event = prepareEventFromSnapshot(dateString, isAnnual, snapshot);

                if (event.isReminderOn()) {
                    tryScheduleNotification(context, event);
                }
            }
        }
    }

    /**
     * Method used to schedule notification for given event with alarm manager
     * @param eventToNotify event data
     * @param context application context
     * @param notification prepared notification
     */
    public static void scheduleNotification(final Event eventToNotify, final Context context,
                                            final Notification notification) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        PendingIntent notificationPendingIntent =
                prepareNotificationPendingIntent(eventToNotify, context, notification);

        ZonedDateTime zonedNotificationDateTime = getZonedReminderDateTime(eventToNotify);
        if (eventToNotify.isAnnual()) {
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

    /**
     * Remove scheduled alarm manager process
     * @param oldEvent old event data to unscheduled notification
     * @param context application context
     */
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
        Log.d("Alarm", "Alarm at " + oldEvent.getDate() +
                oldEvent.getReminderTime() + " cancelled");
    }

    private static void tryScheduleNotification(Context context, Event event) {
        EventController eventController = new EventController(context);
        try {
            NotificationPublisher.scheduleNotification(event, context,
                    eventController.prepareEventNotification(event));
            Log.i("EventReminderOnBootSet", event.getTitle() + " " + event.getDate());
        } catch (ReminderTimeHasPassedException exception) {
            Log.i("Couldn't save reminder", exception.getMessage());
        }
    }

    @NonNull
    private NotificationManager prepareNotificationManager(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelName = "Notification about event";
        NotificationChannel chan = new NotificationChannel("com.calendar",
                channelName, NotificationManager.IMPORTANCE_LOW);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(chan);
        return notificationManager;
    }

    @NonNull
    private static Event prepareEventFromSnapshot(String dateString, boolean isAnnual, DataSnapshot snapshot) {
        String id = snapshot.getKey();
        String title = Objects.requireNonNull(snapshot.child("title").getValue()).toString();
        String note = snapshot.child("note").exists() ? snapshot.child("note").getValue().toString() : "";
        boolean isSystemEvent = parseBoolean(Objects.requireNonNull(snapshot.child("isSystemEvent").getValue()).toString());
        boolean isFree = parseBoolean(Objects.requireNonNull(snapshot.child("isFree").getValue()).toString());
        boolean isReminderOn = parseBoolean(Objects.requireNonNull(snapshot.child("isReminderOn").getValue()).toString());
        LocalTime reminderTimer = snapshot.child("reminderTime").exists() ? getReminderTime(snapshot) : null;

        return new Event(
                id,
                title,
                dateString,
                note,
                isSystemEvent,
                isAnnual,
                isFree,
                isReminderOn,
                reminderTimer);
    }

    private static PendingIntent prepareNotificationPendingIntent(Event eventToNotify,
                                                                  Context context,
                                                                  Notification notification) {
        Intent notificationPublisherIntent = new Intent(context, NotificationPublisher.class);
        int newNotificationId = eventToNotify.getId()
                .hashCode();
        notificationPublisherIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, newNotificationId);
        notificationPublisherIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        return PendingIntent.getBroadcast(context,
                newNotificationId, notificationPublisherIntent, 0);
    }

    private static ZonedDateTime getZonedReminderDateTime(Event eventToNotify) {
        LocalTime reminderTime = eventToNotify.getReminderTime();

        if (reminderTime == null) {
            reminderTime = DEFAULT_REMINDER_TIME;
        }

        LocalDate reminderDate = prepareReminderDate(eventToNotify);

        return LocalDateTime.of(reminderDate, reminderTime)
                            .atZone(ZoneId.systemDefault());
    }

    private static LocalDate prepareReminderDate(Event eventToNotify) {
        String eventDate = eventToNotify.getDate();
        LocalDate currentDate = LocalDate.now();
        if (eventToNotify.isAnnual()) {
            eventDate = prepareNotAnnualDateString(eventDate);
        }

        LocalDate reminderDate = LocalDate.parse(eventDate);
        if (reminderDate.isBefore(currentDate)) {
            reminderDate = addYearIfAnnualOrThrowException(eventToNotify, reminderDate);
        }
        return reminderDate;
    }

    @NonNull
    private static String prepareNotAnnualDateString(String eventDate) {
        LocalDate currentDate = LocalDate.now();
        eventDate = eventDate.replace("XXXX", CalendarUtils.yearFromDate(currentDate));
        return eventDate;
    }

    private static LocalDate addYearIfAnnualOrThrowException(Event eventToNotify,
                                                             LocalDate reminderDate) {
        if (eventToNotify.isAnnual()) {
            reminderDate = reminderDate.plusYears(1);
        } else {
            throw new ReminderTimeHasPassedException(eventToNotify.getId());
        }
        return reminderDate;
    }
}
