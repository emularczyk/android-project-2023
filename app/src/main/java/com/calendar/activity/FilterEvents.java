package com.calendar.activity;

import static android.content.ContentValues.TAG;
import static com.calendar.CalendarUtils.convertDateStringToRegardlessOfTheYear;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.calendar.EventAdapter;
import com.calendar.CalendarUtils;
import com.calendar.Event;
import com.calendar.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity with filtered events
 */
public class FilterEvents extends AppCompatActivity {

    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReference("Calendar");
    private static final ArrayList<Event> eventList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;
    private TextView dateRangeText;
    private MaterialDatePicker<Pair<Long, Long>> materialDatePicker;
    LocalDate startLocalDate;
    LocalDate endLocalDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_events);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setGuiComponents();
        setMaterialDatePicker();
        setEventRecyclerView();
    }

    private void setEventListView() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (startLocalDate != null && endLocalDate != null) {
                    eventList.clear();
                    getEventsFromSnapshot(dataSnapshot);
                    setAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "calendarClicked - onCancelled", error.toException());
            }
        });
    }

    private void setAdapter(){
        eventAdapter = new EventAdapter(eventList, FilterEvents.this);
        recyclerView.setAdapter(eventAdapter);
    }

    public void setEventRecyclerView() {
        eventAdapter = new EventAdapter(eventList, FilterEvents.this);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FilterEvents.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(FilterEvents.this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(eventAdapter);
    }

    private void setGuiComponents() {
        dateRangeText = findViewById(R.id.dateRangeText);
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
            startLocalDate = CalendarUtils.dateToLocalDate(startDate);
            endLocalDate = CalendarUtils.dateToLocalDate(endDate);
            setEventListView();
        });
    }

    private void getEventsFromSnapshot(DataSnapshot dataSnapshot) {
        for (LocalDate date = startLocalDate; date.isBefore(endLocalDate.plusDays(1)); date = date.plusDays(1)) {
            CalendarUtils.getCurrentEventsFromSnapshot(dataSnapshot, date.toString(), eventList);
            CalendarUtils.getCurrentEventsFromSnapshot(dataSnapshot, convertDateStringToRegardlessOfTheYear(date.toString()), eventList);
        }
    }

    public void selectDateRange(View view) {
        materialDatePicker.show(getSupportFragmentManager(), "tag_picker");
    }

}