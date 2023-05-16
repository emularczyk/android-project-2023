package com.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {

    public static LocalDate selectedDate = LocalDate.now();

    public static String convertDateStringToRegardlessOfTheYear(String date) {
        date = date.substring(4);
        date = "XXXX" + date;
        return date;
    }

    public static String getCurrentDateString() {
        return CalendarUtils.dateToString(CalendarUtils.selectedDate);
    }

    public static LocalDate dateToLocalDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return LocalDate.of(year, month, day);
    }

    public static Long getMilliseconds(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atStartOfDay();
        ZonedDateTime zoneDataTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        return zoneDataTime.toInstant().toEpochMilli();
    }

    public static String dateToString(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public static String yearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        return date.format(formatter);
    }

    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate)
    {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = mondayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate))
        {
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }

    public static ArrayList<ArrayList<LocalDate>> daysInMonthInYearArray(LocalDate selectedDate)
    {
        ArrayList<ArrayList<LocalDate>> months = new ArrayList<>();
        LocalDate currentYearStart = yearStart(selectedDate);
        LocalDate currentYearEnd = currentYearStart.plusYears(1);

        while (currentYearStart.isBefore(currentYearEnd)) {
            ArrayList<LocalDate> daysInMonth = new ArrayList<>();
            LocalDate blankDays = mondayForDate(currentYearStart);
            LocalDate endDate = currentYearStart.plusMonths(1);
            while (blankDays.isBefore(currentYearStart)) {
                daysInMonth.add(null);
                blankDays = blankDays.plusDays(1);
            }

            while (currentYearStart.isBefore(endDate)) {
                daysInMonth.add(currentYearStart);
                currentYearStart = currentYearStart.plusDays(1);
            }
            months.add(daysInMonth);
        }
        return months;
    }

    private static LocalDate mondayForDate(LocalDate current)
    {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo))
        {
            if(current.getDayOfWeek() == DayOfWeek.MONDAY)
                return current;

            current = current.minusDays(1);
        }

        return null;
    }

    private static LocalDate yearStart(LocalDate current)
    {
        LocalDate oneYearAgo = current.minusYears(1);

        while (current.isAfter(oneYearAgo))
        {
            if(current.getDayOfYear() == 1)
                return current;

            current = current.minusDays(1);
        }
        return null;
    }
}
