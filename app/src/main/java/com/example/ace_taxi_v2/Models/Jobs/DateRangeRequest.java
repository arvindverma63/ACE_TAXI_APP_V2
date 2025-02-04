package com.example.ace_taxi_v2.Models.Jobs;

public class DateRangeRequest {
    public String from;
    public String to;

    public DateRangeRequest(String from,String to){
        this.from = from;
        this.to = to;
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
}
