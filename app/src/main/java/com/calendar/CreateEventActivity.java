package com.calendar;

import static android.content.ContentValues.TAG;
import static com.calendar.EventRepository.saveEvent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    EditText titleText;
    DatePicker datePicker;
    EditText noteText;
    CheckBox advancedSettingsCheckBox;
    TimePicker timePicker;
    CheckBox annualCheckBox;
    CheckBox reminderCheckBox;
    CheckBox reminderTimeCheckBox;
    CheckBox freeCheckBox;
    Button saveButton;
    private LocalDate selectedDate;
    private String eventId = UUID.randomUUID().toString();
    private Event oldEvent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        initWidgets();
        setupCalendar();
        setupTimer();
        updateExtras();
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
            } catch (Exception e) {
                Log.i("Error", "Couldn't move event");
            }
        }
        try {
            saveEvent(eventToSave);
        } catch (Exception e) {
            Log.i("Error", "Couldn't save event");
        }
        finish();
    }

    private boolean shouldDeleteOldEvent(Event oldEvent, Event newEvent) {
        if (oldEvent != null) {
            if (oldEvent.isAnnual() != newEvent.isAnnual() ||
                    oldEvent.getDate() != newEvent.getDate()) {
                return true;
            }
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
        saveButton = findViewById(R.id.saveEventButton);
    }

    private void setupCalendar() {
        LocalDate dateFromCalendar = CalendarUtils.selectedDate;
        datePicker.init(dateFromCalendar.getYear(),
                dateFromCalendar.getMonth().getValue() - 1,
                dateFromCalendar.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    }
                });
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