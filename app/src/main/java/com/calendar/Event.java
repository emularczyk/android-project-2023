package com.calendar;

import androidx.annotation.NonNull;

public class Event {

    private String title;
    private String description;

    public Event(String title, String description) {
        if (title == null) {
            this.title = "event";
        }
        this.title = title;
        this.description = description;
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

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
