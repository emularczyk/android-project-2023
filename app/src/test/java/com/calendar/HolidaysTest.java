package com.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.time.LocalDate;

public class HolidaysTest {

    @Test
    public void calculateEasterSunday_isCorrect() {
        Holidays holidays = new Holidays();
        LocalDate expectedOutput = LocalDate.of(2023, 4, 9);

        int year = 2023;
        LocalDate result = holidays.calculateEasterSunday(year);

        assertEquals(expectedOutput, result);
    }

    @Test
    public void calculateEasterSunday_isNotCorrect() {
        Holidays holidays = new Holidays();
        LocalDate expectedOutput = LocalDate.of(2023, 4, 10);

        int year = 2023;
        LocalDate result = holidays.calculateEasterSunday(year);

        assertNotEquals(expectedOutput, result);
    }
}
