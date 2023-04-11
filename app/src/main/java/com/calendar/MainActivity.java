package com.calendar;

import static android.content.ContentValues.TAG;
import static com.calendar.CalendarUtils.convertDateStringToRegardlessOfTheYear;
import static com.calendar.CalendarUtils.getCurrentDateString;
import static com.calendar.EventRepository.saveEvent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText eventTitleEditText;
    private DatabaseReference databaseReference;
    private final ArrayList<Event> eventList = new ArrayList<>();
    private Adapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarView calendarView = findViewById(R.id.calendarView);
        eventTitleEditText = findViewById(R.id.eventNameText);
        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
        notifyChange();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                CalendarUtils.selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                Log.i("CurrentDate", CalendarUtils.formattedDate(CalendarUtils.selectedDate));
                notifyChange();
        });
        setEventRecyclerView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        notifyChange();
    }

    public void buttonSaveEvent(View view) {
        try {
            String eventTitle = eventTitleEditText.getText().toString();
            if (eventTitleEditText.length() == 0) {
                eventTitle = "newEvent";
            }
            saveEvent(new Event(eventTitle, CalendarUtils.selectedDate));
            notifyChange();
        } catch (Exception e) {
            Log.i("Error", "Couldn't save event");
        }
    }

    public void weeklyAction(View view)
    {
        startActivity(new Intent(this, WeekViewActivity.class));
    }

    private void setEventRecyclerView() {
        adapter = new Adapter(eventList, getCurrentDateString());
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
    }

    private void notifyChange() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                getEventsFromSnapshot(dataSnapshot, getCurrentDateString());
                getEventsFromSnapshot(dataSnapshot, convertDateStringToRegardlessOfTheYear(getCurrentDateString()));
                adapter = new Adapter(eventList, getCurrentDateString());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "calendarClicked - onCancelled", error.toException());
            }
        });
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
}
