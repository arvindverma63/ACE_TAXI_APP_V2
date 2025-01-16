package com.example.ace_taxi_v2.Models.Jobs;

public class HistoryJob {
    private final String time;
    private final int customerCount;
    private final String mainAddress;
    private final String subAddress;

    public HistoryJob(String time, int customerCount, String mainAddress, String subAddress) {
        this.time = time;
        this.customerCount = customerCount;
        this.mainAddress = mainAddress;
        this.subAddress = subAddress;
    }

    public String getTime() {
        return time;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public String getMainAddress() {
        return mainAddress;
    }

    public String getSubAddress() {
        return subAddress;
    }
}
