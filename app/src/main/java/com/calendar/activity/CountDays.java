package com.calendar.activity;

import static android.content.ContentValues.TAG;
import static com.calendar.CalendarUtils.convertDateStringToRegardlessOfTheYear;
import static com.calendar.CalendarUtils.dateToLocalDate;
import static java.lang.Boolean.parseBoolean;
import static java.lang.String.valueOf;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.calendar.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class CountDays extends AppCompatActivity {

    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReference("Calendar");

    private TextView dateRangeText;
    private TextView numberOfFreeDaysText;
    private TextView numberOfWorkingDaysText;
    private MaterialDatePicker<Pair<Long, Long>> materialDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_days);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setGuiComponents();
        setMaterialDatePicker();
    }

    private void setGuiComponents() {
        dateRangeText = findViewById(R.id.dateRangeText);
        numberOfFreeDaysText = findViewById(R.id.numberOfFreeDays);
        numberOfWorkingDaysText = findViewById(R.id.numberOfWorkingDays);
    }

    private void setMaterialDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long monthStart = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        long monthEnd = calendar.getTimeInMillis();

        materialDatePicker = MaterialDatePicker.Builder
                .dateRangePicker()
                .setSelection(new Pair<>(monthStart, monthEnd))
                .build();
        setMaterialDatePickerListener();
        materialDatePicker.show(getSupportFragmentManager(), "tag_picker");
    }

    private void setMaterialDatePickerListener() {
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            dateRangeText.setText(materialDatePicker.getHeaderText());
            Date startDate = new Date(selection.first);
            Date endDate = new Date(selection.second);
            LocalDate startLocalDate = dateToLocalDate(startDate);
            LocalDate endLocalDate = dateToLocalDate(endDate);
            countFreeDays(startLocalDate, endLocalDate);
        });
    }

    public void selectDateRange(View view) {
        materialDatePicker.show(getSupportFragmentManager(), "tag_picker");
    }

    private void countFreeDays(LocalDate startDate, LocalDate endDate) {
        final AtomicInteger numberOfFreeDays = new AtomicInteger(0);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
                    if (isFree(dataSnapshot, date)) {
                        numberOfFreeDays.set(numberOfFreeDays.get()+1);
                    }
                }
                setView(startDate,endDate,numberOfFreeDays.get());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to count days.", error.toException());
            }
        });
    }

    private void setView(LocalDate startDate, LocalDate endDate,long numberOfFreeDays) {
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        numberOfFreeDaysText.setText(valueOf(numberOfFreeDays));
        numberOfWorkingDaysText.setText(valueOf(numberOfDays - numberOfFreeDays));
    }

    private boolean isFree(DataSnapshot dataSnapshot, LocalDate date) {
        return isWeekend(date.getDayOfWeek()) || isHoliday(dataSnapshot, valueOf(date));
    }

    private boolean isWeekend(DayOfWeek dayOfWeek) {
        return dayOfWeek.getValue() == 5 || dayOfWeek.getValue() == 6;
    }

    private boolean isHoliday(DataSnapshot dataSnapshot, String date) {
        return checkAllEventsInFolder(dataSnapshot, date) ||
                checkAllEventsInFolder(dataSnapshot, convertDateStringToRegardlessOfTheYear(date));
    }

    boolean checkAllEventsInFolder(DataSnapshot dataSnapshot, String date) {
        for (DataSnapshot snapshot : dataSnapshot.child(date).getChildren()) {
            if (parseBoolean(Objects.requireNonNull(snapshot.child("isFree").getValue()).toString())) {
                return true;
            }
        }
        return false;
    }
}
