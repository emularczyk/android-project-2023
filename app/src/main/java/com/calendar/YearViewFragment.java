package com.calendar;

import static com.calendar.CalendarUtils.daysInMonthInYearArray;
import static com.calendar.CalendarUtils.yearFromDate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class YearViewFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private final ArrayList<RecyclerView> monthsRecyclerView = new ArrayList<>();

    public YearViewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_year_view, container, false);

        addMonthRecyclerViewsToList(view);
        monthYearText = view.findViewById(R.id.yearTextView);

        view.findViewById(R.id.next_year_button)
                .setOnClickListener(this::nextWeekAction);
        view.findViewById(R.id.previous_year_button)
                .setOnClickListener(this::previousWeekAction);

        setYearView();
        return view;
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        Log.d("CurrentDate", CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        setYearView();
        ((MainActivity) getActivity()).notifyChange();
    }

    private void addMonthRecyclerViewsToList(View view) {
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView1));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView2));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView3));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView4));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView5));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView6));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView7));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView8));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView9));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView10));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView11));
        monthsRecyclerView.add(view.findViewById(R.id.calendarRecyclerView12));
    }

    private void setYearView()
    {
        monthYearText.setText(yearFromDate(CalendarUtils.selectedDate));
        ArrayList<ArrayList<LocalDate>> daysInMonthsInYear =
                daysInMonthInYearArray(CalendarUtils.selectedDate);

        for (int monthNumber = 0; monthNumber < 12; monthNumber ++) {
            final int numberOfRows = (int) Math.ceil(daysInMonthsInYear.get(monthNumber).size() / 7.0);
            CalendarAdapter calendarAdapter =
                    new CalendarAdapter(daysInMonthsInYear.get(monthNumber), numberOfRows, this);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 7);
            monthsRecyclerView.get(monthNumber).setLayoutManager(layoutManager);
            monthsRecyclerView.get(monthNumber).setAdapter(calendarAdapter);

            final float scale = getContext().getResources().getDisplayMetrics().density;
            int heightInPixels = (int) (numberOfRows * 28 * scale + 0.5f);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightInPixels);
            monthsRecyclerView.get(monthNumber).setLayoutParams(params);
        }
    }

    private void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusYears(1);
        setYearView();
        ((MainActivity) getActivity()).notifyChange();
    }

    private void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusYears(1);
        setYearView();
        ((MainActivity) getActivity()).notifyChange();
    }
}