package com.app.ace_taxi_v2.Models.Jobs;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

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

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");

                if (dateTime.toLocalDate().isEqual(today)) {
                    result = "Today, " + dateTime.format(dateFormatter);
                } else {
                    result = dateTime.format(dateFormatter);
                }
            } else {
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());

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
        if (arriveBy == null || arriveBy.isEmpty()) {
            return null; // Or return an empty string "" if preferred
        }

        try {
            // Parse the input format: 2025-04-23T01:17:00
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(arriveBy);

            // Format to output: HH:mm (e.g., 01:17)
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return arriveBy; // Fallback to original value if parsing fails
        }
    }
}
