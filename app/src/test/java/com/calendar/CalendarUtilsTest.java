package com.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarUtilsTest {

    @Test
    public void convertDateStringToRegardlessOfTheYear_isCorrect() {
        String input = "2023-05-18";
        String expectedOutput = "XXXX-05-18";

        String actualOutput = CalendarUtils.convertDateStringToRegardlessOfTheYear(input);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void dateToString_isCorrect() {
        LocalDate inputDate = LocalDate.of(2023, 5, 18);
        String expectedOutput = "2023-05-18";

        String actualOutput = CalendarUtils.dateToString(inputDate);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void dateToLocalDate_isCorrect() {
        LocalDate expectedOutput = LocalDate.now();
        Date currentDate = new Date();

        LocalDate actualOutput = CalendarUtils.dateToLocalDate(currentDate);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void dateToLocalDate_isNotCorrect() {
        LocalDate expectedOutput = LocalDate.of(2023, 5, 18);
        Date currentDate = new Date(100, 4, 18); // 2000-05-18

        LocalDate actualOutput = CalendarUtils.dateToLocalDate(currentDate);

        assertNotEquals(expectedOutput, actualOutput);
    }

    @Test
    public void getCurrentDateString_isCorrect() {
        LocalDate currentDate = LocalDate.now();
        String expectedOutput = CalendarUtils.dateToString(currentDate);

        String actualOutput = CalendarUtils.getCurrentDateString();

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void getMilliseconds_isCorrect() {
        LocalDate inputDate = LocalDate.of(2023, 5, 18);
        long expectedOutput = inputDate.atStartOfDay()
                .toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now()))
                .toEpochMilli();

        long actualOutput = CalendarUtils.getMilliseconds(inputDate);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void monthYearFromDate_isCorrect() {
        LocalDate inputDate = LocalDate.of(2023, 5, 18);
        String expectedOutput = "maja 2023";

        String actualOutput = CalendarUtils.monthYearFromDate(inputDate);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void yearFromDate_isCorrect() {
        LocalDate inputDate = LocalDate.of(2023, 5, 18);
        String expectedOutput = "2023";

        String actualOutput = CalendarUtils.yearFromDate(inputDate);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void daysInWeekArray_isCorrect() {
        List<LocalDate> expectedOutput = List.of(
                LocalDate.of(2023, 5, 15),
                LocalDate.of(2023, 5, 16),
                LocalDate.of(2023, 5, 17),
                LocalDate.of(2023, 5, 18),
                LocalDate.of(2023, 5, 19),
                LocalDate.of(2023, 5, 20),
                LocalDate.of(2023, 5, 21));

        LocalDate selectedDate = LocalDate.of(2023, 5, 18);
        ArrayList<LocalDate> actualOutput = CalendarUtils.daysInWeekArray(selectedDate);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void daysInWeekArray_isNotCorrect() {
        List<LocalDate> expectedOutput = List.of(
                LocalDate.of(2023, 5, 16),
                LocalDate.of(2023, 5, 17),
                LocalDate.of(2023, 5, 18),
                LocalDate.of(2023, 5, 19),
                LocalDate.of(2023, 5, 20),
                LocalDate.of(2023, 5, 21),
                LocalDate.of(2023, 5, 22));

        LocalDate selectedDate = LocalDate.of(2023, 5, 18);
        ArrayList<LocalDate> actualOutput = CalendarUtils.daysInWeekArray(selectedDate);

        assertNotEquals(expectedOutput, actualOutput);
    }

}
