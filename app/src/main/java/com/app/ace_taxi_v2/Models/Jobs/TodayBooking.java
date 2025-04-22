package com.app.ace_taxi_v2.Models.Jobs;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private List<Vias> vias;
    private String mileage;
    private String arriveBy;
    private int accountNumber;

    public int getAccountNumber() {
        return accountNumber;
    }

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
        return formatToAmPmTime(endTime);
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



    public String getDurationMinutes() {
        return durationMinutes;
    }

    public List<Vias> getVias() {
        return vias;
    }

    private String formatToAmPmTime(String dateTimeStr) {
        if (dateTimeStr == null) return "";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For Android O and above
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault());
                return dateTime.format(formatter);
            } else {
                // For older Android versions
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("hh:mm a", Locale.getDefault());

                java.util.Date date = inputFormat.parse(dateTimeStr);
                return outputFormat.format(date);
            }
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