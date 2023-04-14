package com.calendar;

import static com.calendar.EventRepository.saveEvent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateEventActivity extends AppCompatActivity {

    EditText titleText;
    DatePicker datePicker;
    EditText noteText;
    CheckBox advancedSettingsCheckBox;
    TimePicker timePicker;
    TextView clockText;
    CheckBox annualCheckBox;
    CheckBox reminderCheckBox;
    CheckBox freeCheckBox;
    Button saveButton;
    private LocalDate selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        initWidgets();
        setupCalendar();
        setupTimer();
    }

    private void initWidgets() {
        titleText = findViewById(R.id.title);
        datePicker = findViewById(R.id.date);
        noteText = findViewById(R.id.note);
        timePicker = findViewById(R.id.time);
        advancedSettingsCheckBox = findViewById(R.id.advancedSettings);
        annualCheckBox = findViewById(R.id.annual);
        reminderCheckBox = findViewById(R.id.setReminder);
        freeCheckBox = findViewById(R.id.free);
        saveButton = findViewById(R.id.saveEventButton);
        clockText = findViewById(R.id.clockText);
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
    }

    private void setupTimer() {
        LocalTime currentTimer = LocalTime.now();
        timePicker.setIs24HourView(true);
        timePicker.setHour(currentTimer.getHour());
        timePicker.setMinute(currentTimer.getMinute());
    }

    public void buttonSaveEvent(View view) {
        try {
            saveEvent(new Event(
                    titleText.getText().toString(),
                    selectedDate,
                    noteText.getText().toString()));
        } catch (Exception e) {
            Log.i("Error", "Couldn't save event");
        }
        finish();
    }

    public void showAdvancedOptions(View view) {
        if (advancedSettingsCheckBox.isChecked()) {
            timePicker.setVisibility(View.VISIBLE);
            annualCheckBox.setVisibility(View.VISIBLE);
            reminderCheckBox.setVisibility(View.VISIBLE);
            freeCheckBox.setVisibility(View.VISIBLE);
            clockText.setVisibility(View.VISIBLE);
        } else {
            timePicker.setVisibility(View.INVISIBLE);
            annualCheckBox.setVisibility(View.INVISIBLE);
            reminderCheckBox.setVisibility(View.INVISIBLE);
            freeCheckBox.setVisibility(View.INVISIBLE);
            clockText.setVisibility(View.INVISIBLE);
        }
    }
}
