package com.calendar.activity;

import static android.content.ContentValues.TAG;
import static com.calendar.EventRepository.saveEvent;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.calendar.CalendarUtils;
import com.calendar.Event;
import com.calendar.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    public static final String PACKAGE_NAME = "com.calendar";
    private EditText titleText;
    private DatePicker datePicker;
    private EditText noteText;
    private CheckBox advancedSettingsCheckBox;
    private TimePicker timePicker;
    private CheckBox annualCheckBox;
    private CheckBox reminderCheckBox;
    private CheckBox reminderTimeCheckBox;
    private CheckBox freeCheckBox;
    private LocalDate selectedDate;
    private String eventId = UUID.randomUUID().toString();
    private Event oldEvent = null;
    private InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initWidgets();
        setupCalendar();
        setupTimer();
        updateExtras();
        AdRequest adRequest = new AdRequest.Builder()
                                           .build();
        loadAdd(adRequest);
    }

    public void showAdvancedOptions(View view) {
        if (advancedSettingsCheckBox.isChecked()) {
            annualCheckBox.setVisibility(View.VISIBLE);
            reminderCheckBox.setVisibility(View.VISIBLE);
            freeCheckBox.setVisibility(View.VISIBLE);
        } else {
            annualCheckBox.setVisibility(View.INVISIBLE);
            reminderCheckBox.setVisibility(View.INVISIBLE);
            freeCheckBox.setVisibility(View.INVISIBLE);
        }
    }

    public void showReminderTimeCheckBox(View view) {
        if (reminderCheckBox.isChecked()) {
            reminderTimeCheckBox.setVisibility(View.VISIBLE);
        } else {
            reminderTimeCheckBox.setVisibility(View.INVISIBLE);
        }
    }

    public void showReminderClock(View view) {
        if (reminderTimeCheckBox.isChecked()) {
            timePicker.setVisibility(View.VISIBLE);
        } else {
            timePicker.setVisibility(View.INVISIBLE);
        }
    }

    public void buttonSaveEvent(View view) {
        Event eventToSave = createEvent();
        if (shouldDeleteOldEvent(oldEvent, eventToSave)) {
            try {
                deleteEvent(oldEvent);
                NotificationPublisher.unScheduleNotification(oldEvent, this);
            } catch (Exception e) {
                Log.i("Error", "Couldn't move event");
            }
        }
        try {
            saveEvent(eventToSave);
            NotificationPublisher.scheduleNotification(eventToSave, this,
                                                        getNotification(eventToSave, this));
        } catch (Exception e) {
            Log.i("Error", "Couldn't save event");
        }

        if (interstitialAd != null) {
            setAdAsFullContent();
            interstitialAd.show(getParent());
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
        finish();
    }

    private Notification getNotification(final Event event, Context context) {
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

    private void loadAdd(AdRequest adRequest) {
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        interstitialAd = ad;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        interstitialAd = null;
                    }
                });
    }

    private void setAdAsFullContent() {
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed fullscreen content.");
                interstitialAd = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Log.e(TAG, "Ad failed to show fullscreen content.");
                interstitialAd = null;
            }

            @Override
            public void onAdImpression() {
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
    }

    boolean shouldDeleteOldEvent(Event oldEvent, Event newEvent) {
        if (oldEvent != null) {
            boolean areDatesNotEqual = !Objects.equals(oldEvent.getDate(), newEvent.getDate());
            return oldEvent.isAnnual() != newEvent.isAnnual() ||
                                          areDatesNotEqual;
        }
        return false;
    }

    private void updateExtras() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            oldEvent = (Event) intent.getExtras().getSerializable("event");
            eventId = oldEvent.getId();
            titleText.setText(oldEvent.getTitle());
            noteText.setText(oldEvent.getNote());
            if (containsAdvancedSettings(oldEvent)) {
                advancedSettingsCheckBox.setChecked(true);
            }
            annualCheckBox.setChecked(oldEvent.isAnnual());
            reminderCheckBox.setChecked(oldEvent.isReminderOn());
            freeCheckBox.setChecked(oldEvent.isFreeFromWork());
            if (oldEvent.getReminderTime() != null) {
                reminderTimeCheckBox.setChecked(true);
                timePicker.setHour(oldEvent.getReminderTime().getHour());
                timePicker.setHour(oldEvent.getReminderTime().getMinute());
            }
            reloadViews();
        }
    }

    private boolean containsAdvancedSettings(Event event) {
        return event.isAnnual() || event.isReminderOn() || event.isFreeFromWork();
    }

    private void reloadViews() {
        View view = new View(CreateEventActivity.this);
        showAdvancedOptions(view);
        showReminderTimeCheckBox(view);
        showReminderClock(view);
    }

    private void initWidgets() {
        titleText = findViewById(R.id.title);
        datePicker = findViewById(R.id.date);
        noteText = findViewById(R.id.note);
        timePicker = findViewById(R.id.time);
        advancedSettingsCheckBox = findViewById(R.id.advancedSettings);
        annualCheckBox = findViewById(R.id.annual);
        reminderCheckBox = findViewById(R.id.setReminder);
        reminderTimeCheckBox = findViewById(R.id.reminderTimeCheckBox);
        freeCheckBox = findViewById(R.id.free);
    }

    private void setupCalendar() {
        LocalDate dateFromCalendar = CalendarUtils.selectedDate;
        datePicker.init(dateFromCalendar.getYear(),
                dateFromCalendar.getMonth().getValue() - 1,
                dateFromCalendar.getDayOfMonth(),
                (datePicker, year, month, dayOfMonth) ->
                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth));
        selectedDate = CalendarUtils.selectedDate;
        timePicker.setHour(LocalTime.now().getHour());
        timePicker.setHour(LocalTime.now().getMinute());
    }

    private void setupTimer() {
        LocalTime currentTimer = LocalTime.now();
        timePicker.setIs24HourView(true);
        timePicker.setHour(currentTimer.getHour());
        timePicker.setMinute(currentTimer.getMinute());
    }

    private void deleteEvent(Event event) {
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Calendar");
        Query eventQuery = databaseReference.child(event.getDate()).child(event.getId());
        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "edit canceled", databaseError.toException());
            }
        });
    }

    private Event createEvent() {
        Event event = new Event(
                eventId,
                titleText.getText().toString(),
                selectedDate.toString(),
                noteText.getText().toString());
        if (advancedSettingsCheckBox.isChecked()) {
            event.setAnnual(annualCheckBox.isChecked());
            event.setFreeFromWork(freeCheckBox.isChecked());
            event.setReminderOn(reminderCheckBox.isChecked());
        }
        if (reminderTimeCheckBox.isChecked()) {
            event.setReminderTime(LocalTime.of(timePicker.getHour(), timePicker.getMinute()));
        }
        return event;
    }
}
