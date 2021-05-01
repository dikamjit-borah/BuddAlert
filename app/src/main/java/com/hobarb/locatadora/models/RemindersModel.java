package com.hobarb.locatadora.models;

public class RemindersModel {

    String eventDate, eventTime, eventLocation, eventName;

    public RemindersModel(String eventDate, String eventTime, String eventLocation, String eventName) {
    this.eventDate = eventDate;
    this.eventTime = eventTime;
    this.eventLocation = eventLocation;
    this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
