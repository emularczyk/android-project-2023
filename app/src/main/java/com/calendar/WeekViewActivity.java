package com.calendar;

import static android.content.ContentValues.TAG;
import static com.calendar.CalendarUtils.daysInWeekArray;
import static com.calendar.CalendarUtils.getCurrentDateString;
import static com.calendar.CalendarUtils.monthYearFromDate;
import static com.calendar.CalendarUtils.convertDateStringToRegardlessOfTheYear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class WeekViewActivity extends AppCompatActivity implements WeeklyCalendarAdapter.OnItemListener
{
    private TextView monthYearText;
    private EditText eventTitleEditText;
    private RecyclerView calendarRecyclerView;
    private RecyclerView eventRecyclerView;

    private DatabaseReference databaseReference;
    private final ArrayList<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
        initWidgets();
        setWeekView();
        notifyChange();

        eventTitleEditText = findViewById(R.id.eventNameText);
        Button saveEventButton = findViewById(R.id.saveEventButton);
        saveEventButton.setOnClickListener(v -> saveEvent());

    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        eventRecyclerView = findViewById(R.id.eventRecyclerView);
    }

    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        WeeklyCalendarAdapter calendarAdapter = new WeeklyCalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }


    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        Log.i("CurrentDate", CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        setWeekView();
        notifyChange();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setEventAdapter();
    }

    public void monthlyAction(View view)
    {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void notifyChange() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                getEventsFromSnapshot(dataSnapshot, getCurrentDateString());
                getEventsFromSnapshot(dataSnapshot, convertDateStringToRegardlessOfTheYear(getCurrentDateString()));
                setEventAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "calendarClicked - onCancelled", error.toException());
            }
        });
    }

    private void setEventAdapter()
    {
        Adapter eventAdapter = new Adapter(eventList, getCurrentDateString());
        Log.i("Events", String.valueOf(eventList.size()));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        eventRecyclerView.setLayoutManager(layoutManager);
        eventRecyclerView.setAdapter(eventAdapter);
    }

    private void getEventsFromSnapshot(DataSnapshot dataSnapshot, String fromDate) {
        for (DataSnapshot snapshot : dataSnapshot.child(fromDate).getChildren()) {
            Event event = new Event(
                    snapshot.getKey(),
                    CalendarUtils.selectedDate,
                    Objects.requireNonNull(snapshot.child("description").getValue()).toString());
            eventList.add(event);
        }
    }

    private void saveEvent() {
        try {
            String eventTitle = eventTitleEditText.getText().toString();
            if (eventTitleEditText.length() == 0) {
                eventTitle = "newEvent";
            }
            EventRepository.saveEvent(new Event(eventTitle, CalendarUtils.selectedDate));
            notifyChange();
        } catch (Exception e) {
            Log.i("Error", "Couldn't save event");
        }
    }
}