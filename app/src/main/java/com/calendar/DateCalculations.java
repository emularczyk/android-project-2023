package com.calendar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateCalculations {

    public static LocalDate getCurrentLocalDate() {
        Date currentDate = Calendar.getInstance().getTime();
        LocalDate currentLocalDate = convertToLocalDate(currentDate);
        return currentLocalDate;
    }

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static String convertDateStringToRegardlessOfTheYear(String date) {
        date = date.substring(4);
        date = "XXXX" + date;
        return date;
    }

}
