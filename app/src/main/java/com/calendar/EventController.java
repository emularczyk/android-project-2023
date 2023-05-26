package com.calendar;

import android.app.Notification;
import android.content.Context;

public class EventController {

    private static final String PACKAGE_NAME = "com.calendar";
    private Context context;

    public EventController(Context context) {
        this.context = context;
    }

    public Notification prepareEventNotification(final Event event) {
        Notification.Builder builder
                = new Notification.Builder(context, PACKAGE_NAME);
        builder.setContentTitle(event.getTitle());
        builder.setContentText(event.getNote());
        builder.setSubText(getNotificationDateTime(event));
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        return builder.build();
    }

    private String getNotificationDateTime(final Event event) {
        String notificationDateTime;
        if (event.isAnnual()) {
            String eventDateWithoutYear = event.getDate()
                    .substring(event.getDate()
                            .indexOf("-") + 1);
            notificationDateTime = "Each year " + eventDateWithoutYear;
        } else {
            return event.getDate();
        }

        if (event.getReminderTime() != null) {
            notificationDateTime += " " + event.getReminderTime();
        }

        return notificationDateTime;
    }
}
