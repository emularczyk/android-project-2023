package com.calendar;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {

    private String title;
    private LocalDate date;
    private String note;
    private LocalTime time;
    private boolean isAnnual = false;
    private boolean isFreeFromWork = false;
    private boolean isReminderOn = false;

    public Event(String title, LocalDate date) {
        this(title, date, "");
    }

    public Event(String title, LocalDate date, String description) {
        this.title = title == null ? "wydarzenie" : title.length() == 0 ? "wydarzenie" : title;
        this.note = description;
        this.date = date;
    }

    public Event(String title, LocalDate date, String note, LocalTime time, boolean isAnnual, boolean isFreeFromWork, boolean isReminderOn) {
        this(title, date, note);
        this.time = time;
        this.isAnnual = isAnnual;
        this.isFreeFromWork = isFreeFromWork;
        this.isReminderOn = isReminderOn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", date=" + date +
                ", note='" + note + '\'' +
                ", time=" + time +
                ", isAnnual=" + isAnnual +
                ", isFreeFromWork=" + isFreeFromWork +
                ", isReminderOn=" + isReminderOn +
                '}';
    }
}
