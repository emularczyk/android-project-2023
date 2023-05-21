package com.calendar.view;

import static com.calendar.CalendarUtils.daysInWeekArray;
import static com.calendar.CalendarUtils.monthYearFromDate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.calendar.CalendarAdapter;
import com.calendar.CalendarUtils;
import com.calendar.activity.MainActivity;
import com.calendar.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewFragment extends Fragment implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @Override
    public void onResume() {
        setWeekView();
        ((MainActivity)getActivity()).notifyChange();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_week_view, container, false);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        view.findViewById(R.id.next_week_button)
                .setOnClickListener(this::nextWeekAction);
        view.findViewById(R.id.previous_week_button)
                .setOnClickListener(this::previousWeekAction);
        setWeekView();
        ((MainActivity) getActivity()).notifyChange();
        return view;
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        Log.d("CurrentDate", CalendarUtils.dateToString(CalendarUtils.selectedDate));
        setWeekView();
        ((MainActivity) getActivity()).notifyChange();
    }

    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
        ((MainActivity) getActivity()).notifyChange();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
        ((MainActivity) getActivity()).notifyChange();
    }

    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, 1, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }
}