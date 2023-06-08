package com.calendar;

import static com.calendar.EventRepository.saveEventList;
import static com.calendar.EventRepository.saveEventListRegardlessOfTheYear;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class with system events
 */
public class Holidays {

    /**
     * Saves list with non fluid events in event repository
     */
    public static void addHolidays() {
        List<Event> holidays = new ArrayList<>();

        holidays.add(new Event(
                "Nowy Rok",
                LocalDate.of(1, 1, 1).toString(),
                true));
        holidays.add(new Event(
                "Święto Trzech Króli",
                LocalDate.of(2023, 1, 6).toString(),
                true));
        holidays.add(new Event(
                "Święto Pracy",
                LocalDate.of(1, 5, 1).toString(),
                true));
        holidays.add(new Event(
                "Święto Konstytucji 3 Maja",
                LocalDate.of(1, 5, 3).toString()
                , true));
        holidays.add(new Event(
                "Wniebowzięcie Najświętszej Maryi Panny",
                LocalDate.of(1, 8, 15).toString(),
                true));
        holidays.add(new Event(
                "Wszystkich Świętych",
                LocalDate.of(1, 11, 1).toString(),
                true));
        holidays.add(new Event(
                "Święto Niepodległości",
                LocalDate.of(1, 11, 11).toString()
                , true));
        holidays.add(new Event(
                " Boże Narodzenie",
                LocalDate.of(1, 12, 25).toString()
                , true));
        holidays.add(new Event(
                "Drugi dzień Bożego Narodzenia",
                LocalDate.of(1, 12, 26).toString()
                , true));

        saveEventListRegardlessOfTheYear(holidays);
    }

    /**
     * Saves list with fluid events in event repository
     * @param year a year which the event list should be returned
     */
    public static void addFluidHolidays(int year) {
        List<Event> fluidHolidays = new ArrayList<>();

        LocalDate easterSundayDate = calculateEasterSunday(year);

        fluidHolidays.add(new Event(
                "Wielkanoc",
                easterSundayDate.toString(),
                true));
        fluidHolidays.add(new Event(
                "Poniedziałek Wielkanocny",
                easterSundayDate.plusDays(1).toString(),
                true));
        fluidHolidays.add(new Event(
                "Zielone Świątki",
                easterSundayDate.plusWeeks(7).toString(),
                true));
        fluidHolidays.add(new Event(
                "Boże Ciało",
                easterSundayDate.plusWeeks(9).minusDays(3).toString(),
                true));

        saveEventList(fluidHolidays);
    }

    /**
     * Calculates Easter sunday for given year
     * @param year a year for which the Easter sunday will be returned
     * @return the Easter sunday in a given year
     */
    static LocalDate calculateEasterSunday(int year) {
        int d = calculateD(year);
        int e = calculateE(year, d);
        int q = calculateQ(d, e);
        int month = 3;

        if (q > 31) {
            q -= 31;
            month++;
        }
        return LocalDate.of(year, month, q);
    }

    private static int calculateD(int year) {
        int d = 255 - 11 * (year % 19);
        while (d > 50) {
            d -= 30;
        }
        if (d > 48) {
            d -= 1;
        }
        return d;
    }

    private static int calculateE(int year, int d) {
        return (year + (year / 4) + d + 1) % 7;
    }

    private static int calculateQ(int d, int e) {
        return d + 7 - e;
    }
}
