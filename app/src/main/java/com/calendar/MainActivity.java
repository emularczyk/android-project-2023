package com.calendar;

import static android.content.ContentValues.TAG;
import static com.calendar.CalendarUtils.convertDateStringToRegardlessOfTheYear;
import static com.calendar.CalendarUtils.getCurrentDateString;
import static com.calendar.CalendarUtils.selectedDate;
import static java.lang.Boolean.parseBoolean;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference databaseReference;
    private static final ArrayList<Event> eventList = new ArrayList<>();
    private Adapter adapter;
    private RecyclerView recyclerView;
    private FragmentManager fragmentManager;
    private DrawerLayout drawer;
    private NavigationView drawerNavigation;
    private int currentSelectedFragment = R.id.month_view;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        drawerNavigation = findViewById(R.id.navigationView);

        drawerToggle = new ActionBarDrawerToggle(this, drawer,
                R.string.drawer_open, R.string.drawer_close);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerNavigation.setNavigationItemSelectedListener(this);
        drawerNavigation.bringToFront();

        drawerNavigation.setCheckedItem(currentSelectedFragment);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.calendarFragmentContainer, MonthlyViewFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("monthView") // name can be null
                .commit();

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
        setEventRecyclerView();
    }

    public void createEvent(View view) {
        startActivity(new Intent(this, CreateEventActivity.class));
    }

    public void setEventRecyclerView() {
        adapter = new Adapter(eventList, getCurrentDateString(), MainActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    public void notifyChange() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                getEventsFromSnapshot(dataSnapshot, getCurrentDateString());
                getEventsFromSnapshot(dataSnapshot, convertDateStringToRegardlessOfTheYear(getCurrentDateString()));
                adapter = new Adapter(eventList, getCurrentDateString(), MainActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "calendarClicked - onCancelled", error.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return isDrawerNotNullAndItemSelectedOrSuperItemSelected(item);
            case R.id.jumpToDate:
                DatePickerDialog datePicker = new DatePickerDialog(
                        this,
                        android.R.style.Theme_Light_Panel,
                        (datePicker1, year, month, day) -> {
                            CalendarUtils.selectedDate = LocalDate.of(year, month + 1, day);
                            onResume();
                        },
                        selectedDate.getYear(),
                        selectedDate.getMonth().getValue() - 1,
                        selectedDate.getDayOfMonth());
                datePicker.getWindow()
                          .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePicker.show();
                return true;
            case R.id.countFreeDays:
                Toast.makeText(this, "Count free days is not implemented yet", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.filerEvents:
                Toast.makeText(this, "Filtering events is not implemented yet", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isDrawerNotNullAndItemSelectedOrSuperItemSelected(@NonNull MenuItem item) {
        return drawerToggle != null && drawerToggle.onOptionsItemSelected(item)
                                    || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.week_view:
                currentSelectedFragment = R.id.week_view;
                fragmentManager.beginTransaction()
                        .replace(R.id.calendarFragmentContainer, WeekViewFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("weekView")
                        .commit();
                break;
            case R.id.month_view:
                currentSelectedFragment = R.id.month_view;
                fragmentManager.beginTransaction()
                    .replace(R.id.calendarFragmentContainer, MonthlyViewFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("monthView")
                    .commit();
                break;
            case R.id.year_view:
                currentSelectedFragment = R.id.year_view;
                fragmentManager.beginTransaction()
                        .replace(R.id.calendarFragmentContainer, YearViewFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("yearView")
                        .commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void getEventsFromSnapshot(DataSnapshot dataSnapshot, String fromDate) {
        for (DataSnapshot snapshot : dataSnapshot.child(fromDate).getChildren()) {
            String id = snapshot.getKey();
            String title = Objects.requireNonNull(snapshot.child("title").getValue()).toString();
            String date = fromDate;
            String note = snapshot.child("note").exists() ? snapshot.child("note").getValue().toString() : "";
            Boolean isSystemEvent = parseBoolean(Objects.requireNonNull(snapshot.child("isSystemEvent").getValue()).toString());
            Boolean isAnnual = fromDate.contains("XXXX-");
            Boolean isFree = parseBoolean(Objects.requireNonNull(snapshot.child("isSystemEvent").getValue()).toString());
            Boolean isReminderOn = parseBoolean(Objects.requireNonNull(snapshot.child("isSystemEvent").getValue()).toString());
            LocalTime reminderTimer = snapshot.child("reminderTime").exists() ?getReminderTime(snapshot): null;

            Event event = new Event(
                    id,
                    title,
                    date,
                    note,
                    isSystemEvent,
                    isAnnual,
                    isFree,
                    isReminderOn,
                    reminderTimer);
            eventList.add(event);
        }
    }

    private LocalTime getReminderTime(DataSnapshot snapshot){
        return  LocalTime.of(Integer.parseInt(snapshot.child("reminderTime").child("hour").getValue().toString()),
                Integer.parseInt(snapshot.child("reminderTime").child("minute").getValue().toString()));
    }
    public static ArrayList<Event> getEventList() {
        return eventList;
    }
}
