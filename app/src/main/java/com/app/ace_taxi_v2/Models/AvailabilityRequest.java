package com.app.ace_taxi_v2.Models;

import java.time.Instant;

public class AvailabilityRequest {
    private int userId;
    private String date;
    private String from;
    private String to;
    private boolean giveOrTake;
    private int type;
    private String note;

    public AvailabilityRequest(int userId, String date, String from, String to, boolean giveOrTake, int type, String note) {
        this.userId = userId;
        this.date = date;
        this.from = from;
        this.to = to;
        this.giveOrTake = giveOrTake;
        this.type = type;
        this.note = note;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isGiveOrTake() {
        return giveOrTake;
    }

    public void setGiveOrTake(boolean giveOrTake) {
        this.giveOrTake = giveOrTake;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
