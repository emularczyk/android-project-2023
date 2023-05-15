package com.calendar.View;

import static com.calendar.CalendarUtils.getMilliseconds;
import static com.calendar.CalendarUtils.selectedDate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.calendar.CalendarUtils;
import com.calendar.MainActivity;
import com.calendar.R;

import java.time.LocalDate;

public class MonthlyViewFragment extends Fragment {

    private CalendarView calendarView;

    @Override
    public void onResume() {
        calendarView.setDate(getMilliseconds(selectedDate));
        ((MainActivity)getActivity()).notifyChange();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_monthly_view, container, false);
        calendarView = view.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener((dateView, year, month, dayOfMonth) -> {
            CalendarUtils.selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            ((MainActivity) getActivity()).notifyChange();
        });

        return view;
    }
}