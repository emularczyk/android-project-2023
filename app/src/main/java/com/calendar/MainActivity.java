package com.calendar;

import static android.content.ContentValues.TAG;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText eventTitleEditText;
    private String selectedDateString;
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
        selectedDateString = getCurrentDateString();
        notifyChange();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                selectedDateString = dateToString(year, month, dayOfMonth);
                notifyChange();
        });

        adapter = new Adapter(eventList, selectedDateString);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
    }

    private String getCurrentDateString() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.ROOT);
        return dateFormat.format(currentDate);
    }

    private String dateToString(int year, int month, int dayOfMonth) {
        return dayOfMonth + "-" + (month + 1) + "-" + year;
    }

    private void notifyChange() {
        databaseReference.child(selectedDateString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = new Event(snapshot.getKey(),
                            Objects.requireNonNull(snapshot.child("description").getValue()).toString());
                    eventList.add(event);
                }
                adapter = new Adapter(eventList, selectedDateString);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "calendarClicked - onCancelled", error.toException());
            }
        });
    }

    public void buttonSaveEvent(View view) {
        try {
            String eventTitle = eventTitleEditText.getText().toString();
            if (eventTitleEditText.length() == 0) {
                eventTitle = "newEvent";
            }
            databaseReference.child(selectedDateString)
                    .child(eventTitle)
                    .child("description")
                    .setValue("");
            notifyChange();
        } catch (Exception e) {
            Log.i("Error", "Couldn't save event");
        }
    }
}
