package com.example.ace_taxi_v2.Models.Jobs;

import java.time.LocalDateTime;
import java.util.List;

public class DateRangeResponse {
    private String regNo;
    private String backgroundColorRGB;
    private String fullname;
    private int bookingId;
    private int userId;
    private Integer suggestedUserId;
    private boolean cancelledOnArrival;
    private String cellText;
    private String hasDetails;
    private int status;
    private LocalDateTime endTime;
    private LocalDateTime dateCreated;
    private String bookedByName;
    private boolean cancelled;
    private String details;
    private String email;
    private int durationMinutes;
    private boolean isAllDay;
    private String passengerName;
    private int passengers;
    private int paymentStatus;
    private int confirmationStatus;
    private int scope;
    private String phoneNumber;
    private String pickupAddress;
    private LocalDateTime pickupDateTime;
    private String pickupPostCode;
    private String destinationAddress;
    private String destinationPostCode;
    private List<String> vias;
    private String recurrenceException;
    private Integer recurrenceID;
    private String recurrenceRule;
    private String updatedByName;
    private String cancelledByName;
    private double price;
    private double priceAccount;
    private Integer mileage;
    private String mileageText;
    private String durationText;
    private boolean chargeFromBase;
    private int actionByUserId;
    private int accountNumber;
    private double parkingCharge;
    private int waitingTimeMinutes;
    private LocalDateTime paymentLinkSentOn;
    private String paymentLinkSentBy;
    private boolean isASAP;

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getSuggestedUserId() {
        return suggestedUserId;
    }

    public void setSuggestedUserId(Integer suggestedUserId) {
        this.suggestedUserId = suggestedUserId;
    }

    public boolean isCancelledOnArrival() {
        return cancelledOnArrival;
    }

    public void setCancelledOnArrival(boolean cancelledOnArrival) {
        this.cancelledOnArrival = cancelledOnArrival;
    }

    public String getCellText() {
        return cellText;
    }

    public void setCellText(String cellText) {
        this.cellText = cellText;
    }

    public String getHasDetails() {
        return hasDetails;
    }

    public void setHasDetails(String hasDetails) {
        this.hasDetails = hasDetails;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getBookedByName() {
        return bookedByName;
    }

    public void setBookedByName(String bookedByName) {
        this.bookedByName = bookedByName;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getConfirmationStatus() {
        return confirmationStatus;
    }

    public void setConfirmationStatus(int confirmationStatus) {
        this.confirmationStatus = confirmationStatus;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public LocalDateTime getPickupDateTime() {
        return pickupDateTime;
    }

    public void setPickupDateTime(LocalDateTime pickupDateTime) {
        this.pickupDateTime = pickupDateTime;
    }

    public String getPickupPostCode() {
        return pickupPostCode;
    }

    public void setPickupPostCode(String pickupPostCode) {
        this.pickupPostCode = pickupPostCode;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationPostCode() {
        return destinationPostCode;
    }

    public void setDestinationPostCode(String destinationPostCode) {
        this.destinationPostCode = destinationPostCode;
    }

    public List<String> getVias() {
        return vias;
    }

    public void setVias(List<String> vias) {
        this.vias = vias;
    }

    public String getRecurrenceException() {
        return recurrenceException;
    }

    public void setRecurrenceException(String recurrenceException) {
        this.recurrenceException = recurrenceException;
    }

    public Integer getRecurrenceID() {
        return recurrenceID;
    }

    public void setRecurrenceID(Integer recurrenceID) {
        this.recurrenceID = recurrenceID;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public String getCancelledByName() {
        return cancelledByName;
    }

    public void setCancelledByName(String cancelledByName) {
        this.cancelledByName = cancelledByName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPriceAccount() {
        return priceAccount;
    }

    public void setPriceAccount(double priceAccount) {
        this.priceAccount = priceAccount;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getMileageText() {
        return mileageText;
    }

    public void setMileageText(String mileageText) {
        this.mileageText = mileageText;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public boolean isChargeFromBase() {
        return chargeFromBase;
    }

    public void setChargeFromBase(boolean chargeFromBase) {
        this.chargeFromBase = chargeFromBase;
    }

    public int getActionByUserId() {
        return actionByUserId;
    }

    public void setActionByUserId(int actionByUserId) {
        this.actionByUserId = actionByUserId;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getParkingCharge() {
        return parkingCharge;
    }

    public void setParkingCharge(double parkingCharge) {
        this.parkingCharge = parkingCharge;
    }

    public int getWaitingTimeMinutes() {
        return waitingTimeMinutes;
    }

    public void setWaitingTimeMinutes(int waitingTimeMinutes) {
        this.waitingTimeMinutes = waitingTimeMinutes;
    }

    public LocalDateTime getPaymentLinkSentOn() {
        return paymentLinkSentOn;
    }

    public void setPaymentLinkSentOn(LocalDateTime paymentLinkSentOn) {
        this.paymentLinkSentOn = paymentLinkSentOn;
    }

    public String getPaymentLinkSentBy() {
        return paymentLinkSentBy;
    }

    public void setPaymentLinkSentBy(String paymentLinkSentBy) {
        this.paymentLinkSentBy = paymentLinkSentBy;
    }

    public boolean isASAP() {
        return isASAP;
    }

    public void setASAP(boolean ASAP) {
        isASAP = ASAP;
    }
}