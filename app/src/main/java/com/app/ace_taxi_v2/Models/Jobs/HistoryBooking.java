package com.app.ace_taxi_v2.Models.Jobs;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryBooking {
    private String regNo;
    private String backgroundColorRGB;
    private String fullname;
    private int id;
    private String cellText;
    private String endTime;
    private String pickupAddress;
    private String destinationAddress;
    private double price;
    private String pickupPostCode;
    private String destinationPostCode;
    private String passengerName;
    private String pickupDateTime;
    private int scope;
    private String status;
    private int paymentStatus;

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public String getPickupDateTime() {
        try {
            // Parse the ISO 8601 datetime string
            LocalDateTime dateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateTime = LocalDateTime.parse(pickupDateTime);
            }

            // Define the desired output format
            DateTimeFormatter formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
            }

            // Format the parsed datetime and return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return dateTime.format(formatter);
            }
        } catch (Exception e) {
            // Handle parsing errors gracefully
            e.printStackTrace();
            return pickupDateTime; // Return the original if formatting fails
        }
        return pickupDateTime;
    }

    public void setPickupDateTime(String pickupDateTime) {
        this.pickupDateTime = pickupDateTime;
    }

    private int passengers;

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getBackgroundColorRGB() {
        return backgroundColorRGB;
    }

    public void setBackgroundColorRGB(String backgroundColorRGB) {
        this.backgroundColorRGB = backgroundColorRGB;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getBookingId() {
        return id;
    }

    public void setBookingId(int id) {
        this.id = id;
    }

    public String getCellText() {
        return cellText;
    }

    public void setCellText(String cellText) {
        this.cellText = cellText;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public int getId() {
        return id;
    }

    public String getPickupPostCode() {
        return pickupPostCode;
    }

    public String getDestinationPostCode() {
        return destinationPostCode;
    }

    public String getStatus() {
        return status;
    }

    public int getScope() {
        return scope;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }
    public String getScopeText() {
        switch (scope) {
            case 0: return "Cash";
            case 1: return "Account";
            case 2: return "Rank";
            case 3: return "All";
            case 4: return "Card";
            default: return "Unknown";
        }
    }

    public String getPaymentStatusText() {
        switch (paymentStatus) {
            case 0: return "UnPaid";
            case 2: return "Paid";
            case 3: return "Awaiting Payment";
            default: return "Unknown";
        }
    }

}
