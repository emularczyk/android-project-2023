package com.calendar;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

public class Event implements Serializable {

    private String id;
    private String title;
    private String date;
    private String note;
    private boolean isSystemEvent = false;
    private boolean isAnnual = false;
    private boolean isFreeFromWork = false;
    private boolean isReminderOn = false;
    private LocalTime reminderTime = null;

    public Event(String id,String title, String date, String note) {
        this.id = id;
        this.title = title == null ? "Wydarzenie" : title.length() == 0 ? "wydarzenie" : title;
        this.note = note;
        this.date = date;
    }

    public Event(String title, String date, boolean isSystemEvent) {
        this.id = UUID.randomUUID().toString();
        this.title = title == null ? "Wydarzenie" : title.length() == 0 ? "wydarzenie" : title;
        this.date = date;
        this.isSystemEvent = isSystemEvent;
        this.isFreeFromWork = true;
    }

    public Event(String id, String title, String date, String note,
                 boolean isSystemEvent, boolean isAnnual, boolean isFreeFromWork,
                 boolean isReminderOn, LocalTime reminderTime) {
        this.id = id;
        this.title = title == null ? "Wydarzenie" : title.length() == 0 ? "wydarzenie" : title;
        this.note = note;
        this.date = date;
        this.isAnnual = isAnnual;
        this.isFreeFromWork = isFreeFromWork;
        this.isReminderOn = isReminderOn;
        this.reminderTime = reminderTime;
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

    public String getDate() {
        return date;
    }

    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public void setAnnual(boolean annual) {
        isAnnual = annual;
    }

    public void setFreeFromWork(boolean freeFromWork) {
        isFreeFromWork = freeFromWork;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public boolean isAnnual() {
        return isAnnual;
    }

    public boolean isFreeFromWork() {
        return isFreeFromWork;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isReminderOn() {
        return isReminderOn;
    }

    public void setReminderOn(boolean reminderOn) {
        isReminderOn = reminderOn;
    }

    public boolean isSystemEvent() {
        return isSystemEvent;
    }

    public void setSystemEvent(boolean systemEvent) {
        isSystemEvent = systemEvent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                "title='" + title + '\'' +
                ", date=" + date +
                ", note='" + note + '\'' +
                ", time=" + reminderTime +
                ", isAnnual=" + isAnnual +
                ", isFreeFromWork=" + isFreeFromWork +
                '}';
    }
}
