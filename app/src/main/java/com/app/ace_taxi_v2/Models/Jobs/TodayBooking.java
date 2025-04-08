package com.app.ace_taxi_v2.Models.Jobs;

import android.os.Build;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TodayBooking {
    private String regNo;
    private String backgroundColorRGB;
    private String fullname;
    private int bookingId;
    private String cellText;
    private String endTime;
    private String pickupAddress;
    private String destinationAddress;
    private double price;
    private String pickupPostCode;
    private String destinationPostCode;
    private String passengerName;
    private String pickupDateTime;
    private String status;
    private int scope;
    private int paymentStatus;
    private int passengers;
    private String durationMinutes;

    public String getStatus() {
        return status != null ? status : "";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public String getPickupDateTime() {
        return formatDateTime(pickupDateTime);
    }

    public void setPickupDateTime(String pickupDateTime) {
        this.pickupDateTime = pickupDateTime;
    }

    public String getEndTime() {
        return formatDateTime(endTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRegNo() {
        return regNo != null ? regNo : "";
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getBackgroundColorRGB() {
        return backgroundColorRGB != null ? backgroundColorRGB : "";
    }

    public void setBackgroundColorRGB(String backgroundColorRGB) {
        this.backgroundColorRGB = backgroundColorRGB;
    }

    public String getFullname() {
        return fullname != null ? fullname : "";
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getCellText() {
        return cellText != null ? cellText : "";
    }

    public void setCellText(String cellText) {
        this.cellText = cellText;
    }

    public String getPickupAddress() {
        return pickupAddress != null ? pickupAddress : "";
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress != null ? destinationAddress : "";
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
        return passengerName != null ? passengerName : "";
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPickupPostCode() {
        return pickupPostCode != null ? pickupPostCode : "";
    }

    public void setPickupPostCode(String pickupPostCode) {
        this.pickupPostCode = pickupPostCode;
    }

    public String getDestinationPostCode() {
        return destinationPostCode != null ? destinationPostCode : "";
    }

    public void setDestinationPostCode(String destinationPostCode) {
        this.destinationPostCode = destinationPostCode;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
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

    private String formatDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
                return dateTime.format(formatter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a");
                java.util.Date date = inputFormat.parse(dateTimeStr);
                return outputFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dateTimeStr;
    }

    public String getDurationMinutes() {
        return durationMinutes;
    }
}