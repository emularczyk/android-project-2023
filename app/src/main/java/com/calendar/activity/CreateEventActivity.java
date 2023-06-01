package com.calendar.activity;

import static android.content.ContentValues.TAG;
import static com.calendar.CalendarUtils.convertDateStringToRegardlessOfTheYear;
import static com.calendar.EventRepository.saveEvent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.calendar.AdController;
import com.calendar.CalendarUtils;
import com.calendar.Event;
import com.calendar.EventController;
import com.calendar.NotificationPublisher;
import com.calendar.R;
import com.calendar.ReminderTimeHasPassedException;
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

    private AdController adController;
    private EventController eventController;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initWidgets();
        setupCalendar();
        setupTimer();
        updateExtras();
        adController = new AdController(this);
        eventController = new EventController(this);
    }

    public void showAdvancedOptions(View view) {
        if (advancedSettingsCheckBox.isChecked()) {
            annualCheckBox.setVisibility(View.VISIBLE);
            reminderCheckBox.setVisibility(View.VISIBLE);
            freeCheckBox.setVisibility(View.VISIBLE);
            showReminderTimeCheck();
            showReminderClockCheck();
        } else {
            annualCheckBox.setVisibility(View.INVISIBLE);
            reminderCheckBox.setVisibility(View.INVISIBLE);
            freeCheckBox.setVisibility(View.INVISIBLE);
            reminderTimeCheckBox.setVisibility(View.INVISIBLE);
            timePicker.setVisibility(View.INVISIBLE);
        }
    }

    public void showReminderTimeCheckBox(View view) {
        showReminderTimeCheck();
        showReminderClockCheck();
    }

    private void showReminderTimeCheck(){
        if (advancedSettingsCheckBox.isChecked() && reminderCheckBox.isChecked()) {
            reminderTimeCheckBox.setVisibility(View.VISIBLE);
        } else {
            reminderTimeCheckBox.setVisibility(View.INVISIBLE);
        }
    }

    public void showReminderClock(View view) {
        showReminderClockCheck();
    }

    public void showReminderClockCheck() {
        if (advancedSettingsCheckBox.isChecked()  && reminderCheckBox.isChecked() &&  reminderTimeCheckBox.isChecked()) {
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
                if (oldEvent.isReminderOn()) {
                    NotificationPublisher.unScheduleNotification(oldEvent, this);
                }
            } catch (Exception e) {
                Log.i("Error", "Couldn't move event");
            }
        }
        try {
            saveEvent(eventToSave);
            if (eventToSave.isReminderOn()) {
                NotificationPublisher.scheduleNotification(eventToSave, this,
                        eventController.prepareEventNotification(eventToSave));
            }
        } catch (ReminderTimeHasPassedException e) {
            Log.i("Couldn't save reminder", e.getMessage());
        } catch (Exception e) {
            Log.i("Error", "Couldn't save event");
        }

        adController.showFullContentAd(getParent());
        finish();
    }

    boolean shouldDeleteOldEvent(Event oldEvent, Event newEvent) {
        if (oldEvent != null) {
            boolean differentEventDates = Objects.equals(oldEvent.getDate(), convertDateStringToRegardlessOfTheYear(newEvent.getDate()));
            return (oldEvent.isAnnual() != newEvent.isAnnual()) || !differentEventDates;
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
                timePicker.setMinute(oldEvent.getReminderTime().getMinute());
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
            if (reminderTimeCheckBox.isChecked()) {
                event.setReminderTime(LocalTime.of(timePicker.getHour(), timePicker.getMinute()));
            }
        }
        return event;
    }
}
