package com.calendar;

import java.time.LocalDate;

public class Event {

    private String title;
    private String description;
    private LocalDate date;

    public Event(String title, LocalDate date) {
        this(title,date,"");
    }

    public Event(String title, LocalDate date,String description) {
        this.title = title == null ? "wydarzenie" : title;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }
}
