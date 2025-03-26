package com.app.ace_taxi_v2.Models.Jobs;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetBookingInfo {
    private String regNo;
    private String backgroundColorRGB;
    private String fullname;
    private int bookingId;
    private int userId;
    private Integer suggestedUserId;
    private boolean cancelledOnArrival;
    private String cellText;
    private boolean hasDetails;
    private String status;
    private String endTime;
    private String dateCreated;
    private String bookedByName;
    private boolean cancelled;
    private String details;
    private String email;
    private int durationMinutes;
    private boolean isAllDay;
    private String passengerName;
    private int passengers;
    private int paymentStatus;
    private Integer confirmationStatus;
    private int scope;
    private String phoneNumber;
    private String pickupAddress;
    private String pickupDateTime;
    private String arriveBy;
    private String pickupPostCode;
    private String destinationAddress;
    private String destinationPostCode;
    private List<String> vias;
    private String recurrenceException;
    private String recurrenceID;
    private String recurrenceRule;
    private String updatedByName;
    private String cancelledByName;
    private double price;
    private boolean manuallyPriced;
    private double priceDiscount;
    private double priceAccount;
    private double mileage;
    private String mileageText;
    private String durationText;
    private boolean chargeFromBase;
    private int actionByUserId;
    private int accountNumber;
    private double parkingCharge;
    private int waitingTimeMinutes;
    private String paymentLinkSentOn;
    private String paymentLinkSentBy;
    private boolean isASAP;

    // Default constructor (required for Gson)
    public GetBookingInfo() {}

    // Getters with enum mapping
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

    public String getPickupDateTime() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && pickupDateTime != null) {
                LocalDateTime dateTime = LocalDateTime.parse(pickupDateTime);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
                return dateTime.format(formatter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pickupDateTime != null ? pickupDateTime : "";
    }

    // Standard getters and setters (unchanged for brevity, only showing relevant ones)
    public int getScope() { return scope; }
    public void setScope(int scope) { this.scope = scope; }
    public int getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(int paymentStatus) { this.paymentStatus = paymentStatus; }
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public int getPassengers() { return passengers; }
    public void setPassengers(int passengers) { this.passengers = passengers; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }
    public String getPickupPostCode() { return pickupPostCode; }
    public void setPickupPostCode(String pickupPostCode) { this.pickupPostCode = pickupPostCode; }
    public String getDestinationPostCode() { return destinationPostCode; }
    public void setDestinationPostCode(String destinationPostCode) { this.destinationPostCode = destinationPostCode; }
    public double getMileage() { return mileage; }
    public String getMileageText() { return mileageText; }
    public void setMileageText(String mileageText) { this.mileageText = mileageText; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getDurationText() { return durationText; }
    public void setDurationText(String durationText) { this.durationText = durationText; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public double getParkingCharge() { return parkingCharge; }
    public void setParkingCharge(double parkingCharge) { this.parkingCharge = parkingCharge; }
    public int getWaitingTimeMinutes() { return waitingTimeMinutes; }
    public void setWaitingTimeMinutes(int waitingTimeMinutes) { this.waitingTimeMinutes = waitingTimeMinutes; }
    public String getBookedByName() { return bookedByName; }
    public void setBookedByName(String bookedByName) { this.bookedByName = bookedByName; }
    public String getUpdatedByName() { return updatedByName; }
    public void setUpdatedByName(String updatedByName) { this.updatedByName = updatedByName; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public List<String> getVias() { return vias; }
    public void setVias(List<String> vias) { this.vias = vias; }
    public int getAccountNumber() { return accountNumber; }
    public void setAccountNumber(int accountNumber) { this.accountNumber = accountNumber; }

    public String getRegNo() {
        return regNo;
    }

    public String getBackgroundColorRGB() {
        return backgroundColorRGB;
    }

    public String getFullname() {
        return fullname;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getSuggestedUserId() {
        return suggestedUserId;
    }

    public boolean isCancelledOnArrival() {
        return cancelledOnArrival;
    }

    public String getCellText() {
        return cellText;
    }

    public boolean isHasDetails() {
        return hasDetails;
    }

    public String getStatus() {
        return status;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public Integer getConfirmationStatus() {
        return confirmationStatus;
    }

    public String getArriveBy() {
        return arriveBy;
    }

    public String getRecurrenceException() {
        return recurrenceException;
    }

    public String getRecurrenceID() {
        return recurrenceID;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public String getCancelledByName() {
        return cancelledByName;
    }

    public boolean isManuallyPriced() {
        return manuallyPriced;
    }

    public double getPriceDiscount() {
        return priceDiscount;
    }

    public double getPriceAccount() {
        return priceAccount;
    }

    public boolean isChargeFromBase() {
        return chargeFromBase;
    }

    public int getActionByUserId() {
        return actionByUserId;
    }

    public String getPaymentLinkSentOn() {
        return paymentLinkSentOn;
    }

    public String getPaymentLinkSentBy() {
        return paymentLinkSentBy;
    }

    public boolean isASAP() {
        return isASAP;
    }
}