package com.app.ace_taxi_v2.Models.Jobs;

import android.os.Build;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Booking {
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
    private String mileage;
    private String arriveBy;


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
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
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
    public String getFormattedDateTime() {
        return formatDate(pickupDateTime).toString();
    }
    private String formatDate(String dateTimeStr) {
        if (dateTimeStr == null) return "";

        try {
            String result;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
                LocalDate today = LocalDate.now();

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");

                if (dateTime.toLocalDate().isEqual(today)) {
                    result = "Today, " + dateTime.format(dateFormatter);
                } else {
                    result = dateTime.format(dateFormatter);
                }
            } else {
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMM dd yyyy", Locale.getDefault());

                java.util.Date date = inputFormat.parse(dateTimeStr);

                java.text.SimpleDateFormat todayCheckFormat = new java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                String todayStr = todayCheckFormat.format(new java.util.Date());
                String dateStr = todayCheckFormat.format(date);

                if (dateStr.equals(todayStr)) {
                    result = "Today, " + outputFormat.format(date);
                } else {
                    result = outputFormat.format(date);
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return dateTimeStr;
        }
    }

    public String getMileage() {
        return mileage;
    }

    public String getArriveBy() {
        return arriveBy;
    }
}
