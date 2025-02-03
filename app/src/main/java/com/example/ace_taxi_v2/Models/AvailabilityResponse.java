package com.example.ace_taxi_v2.Models;

import java.time.Instant;

public class AvailabilityResponse {
    private int userId;
    private Instant date;
    private AvailabilityRequest.TimeTicks from;
    private AvailabilityRequest.TimeTicks to;
    private boolean giveOrTake;
    private int type;
    private String note;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public AvailabilityRequest.TimeTicks getFrom() {
        return from;
    }

    public void setFrom(AvailabilityRequest.TimeTicks from) {
        this.from = from;
    }

    public AvailabilityRequest.TimeTicks getTo() {
        return to;
    }

    public void setTo(AvailabilityRequest.TimeTicks to) {
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
