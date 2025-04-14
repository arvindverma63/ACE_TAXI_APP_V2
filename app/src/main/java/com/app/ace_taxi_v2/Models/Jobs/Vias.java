package com.app.ace_taxi_v2.Models.Jobs;

public class Vias {
    private int id;
    private int bookingId;
    private String address;
    private String postCode;
    private int viaSequence;
    private String add; // New field added
    private Booking booking;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostCode() {
        return postCode != null ? postCode : "";
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public int getViaSequence() {
        return viaSequence;
    }

    public void setViaSequence(int viaSequence) {
        this.viaSequence = viaSequence;
    }

    public String getAdd() {
        return add != null ? add : "";
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    // Nested Booking class
    public static class Booking {
        private int id;
        private String pickupAddress;
        private String pickupPostCode;
        private String destinationAddress;
        private String destinationPostCode;
        private String details;
        private String passengerName;
        private int passengers;
        private String phoneNumber;
        private String email;
        private String pickupDateTime;
        private String arriveBy;
        private boolean isAllDay;
        private int durationMinutes;
        private String recurrenceRule;
        private String recurrenceID;
        private String recurrenceException;
        private String bookedByName;
        private int confirmationStatus;
        private int paymentStatus;
        private int scope;
        private int accountNumber;
        private String invoiceNumber;
        private double price;
        private double tip;
        private boolean manuallyPriced;
        private double priceAccount;
        private double mileage;
        private String mileageText;
        private String durationText;
        private boolean chargeFromBase;
        private boolean cancelled;
        private boolean cancelledOnArrival;
        private int userId;
        private Integer suggestedUserId;
        private String dateCreated;
        private String dateUpdated;
        private String updatedByName;
        private String cancelledByName;
        private int actionByUserId;
        private String driverInvoiceStatement;
        private String statementId;
        private int vehicleType;
        private int waitingTimeMinutes;
        private double waitingTimePriceDriver;
        private double waitingTimePriceAccount;
        private double parkingCharge;
        private boolean postedForInvoicing;
        private boolean postedForStatement;
        private String allocatedAt;
        private int allocatedById;
        private int status;
        private String paymentOrderId;
        private String paymentLink;
        private String paymentLinkSentBy;
        private String paymentLinkSentOn;
        private boolean paymentReceiptSent;
        private boolean isASAP;
        private String cellText;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPickupAddress() {
            return pickupAddress != null ? pickupAddress : "";
        }

        public void setPickupAddress(String pickupAddress) {
            this.pickupAddress = pickupAddress;
        }

        public String getPickupPostCode() {
            return pickupPostCode != null ? pickupPostCode : "";
        }

        public void setPickupPostCode(String pickupPostCode) {
            this.pickupPostCode = pickupPostCode;
        }

        public String getDestinationAddress() {
            return destinationAddress != null ? destinationAddress : "";
        }

        public void setDestinationAddress(String destinationAddress) {
            this.destinationAddress = destinationAddress;
        }

        public String getDestinationPostCode() {
            return destinationPostCode != null ? destinationPostCode : "";
        }

        public void setDestinationPostCode(String destinationPostCode) {
            this.destinationPostCode = destinationPostCode;
        }

        public String getDetails() {
            return details != null ? details : "";
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getPassengerName() {
            return passengerName != null ? passengerName : "";
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

        public String getPhoneNumber() {
            return phoneNumber != null ? phoneNumber : "";
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email != null ? email : "";
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPickupDateTime() {
            return pickupDateTime != null ? pickupDateTime : "";
        }

        public void setPickupDateTime(String pickupDateTime) {
            this.pickupDateTime = pickupDateTime;
        }

        public String getArriveBy() {
            return arriveBy != null ? arriveBy : "";
        }

        public void setArriveBy(String arriveBy) {
            this.arriveBy = arriveBy;
        }

        public boolean isAllDay() {
            return isAllDay;
        }

        public void setAllDay(boolean allDay) {
            isAllDay = allDay;
        }

        public int getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
        }

        public String getRecurrenceRule() {
            return recurrenceRule != null ? recurrenceRule : "";
        }

        public void setRecurrenceRule(String recurrenceRule) {
            this.recurrenceRule = recurrenceRule;
        }

        public String getRecurrenceID() {
            return recurrenceID != null ? recurrenceID : "";
        }

        public void setRecurrenceID(String recurrenceID) {
            this.recurrenceID = recurrenceID;
        }

        public String getRecurrenceException() {
            return recurrenceException != null ? recurrenceException : "";
        }

        public void setRecurrenceException(String recurrenceException) {
            this.recurrenceException = recurrenceException;
        }

        public String getBookedByName() {
            return bookedByName != null ? bookedByName : "";
        }

        public void setBookedByName(String bookedByName) {
            this.bookedByName = bookedByName;
        }

        public int getConfirmationStatus() {
            return confirmationStatus;
        }

        public void setConfirmationStatus(int confirmationStatus) {
            this.confirmationStatus = confirmationStatus;
        }

        public int getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(int paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public int getScope() {
            return scope;
        }

        public void setScope(int scope) {
            this.scope = scope;
        }

        public int getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(int accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getInvoiceNumber() {
            return invoiceNumber != null ? invoiceNumber : "";
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getTip() {
            return tip;
        }

        public void setTip(double tip) {
            this.tip = tip;
        }

        public boolean isManuallyPriced() {
            return manuallyPriced;
        }

        public void setManuallyPriced(boolean manuallyPriced) {
            this.manuallyPriced = manuallyPriced;
        }

        public double getPriceAccount() {
            return priceAccount;
        }

        public void setPriceAccount(double priceAccount) {
            this.priceAccount = priceAccount;
        }

        public double getMileage() {
            return mileage;
        }

        public void setMileage(double mileage) {
            this.mileage = mileage;
        }

        public String getMileageText() {
            return mileageText != null ? mileageText : "";
        }

        public void setMileageText(String mileageText) {
            this.mileageText = mileageText;
        }

        public String getDurationText() {
            return durationText != null ? durationText : "";
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

        public boolean isCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public boolean isCancelledOnArrival() {
            return cancelledOnArrival;
        }

        public void setCancelledOnArrival(boolean cancelledOnArrival) {
            this.cancelledOnArrival = cancelledOnArrival;
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

        public String getDateCreated() {
            return dateCreated != null ? dateCreated : "";
        }

        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        public String getDateUpdated() {
            return dateUpdated != null ? dateUpdated : "";
        }

        public void setDateUpdated(String dateUpdated) {
            this.dateUpdated = dateUpdated;
        }

        public String getUpdatedByName() {
            return updatedByName != null ? updatedByName : "";
        }

        public void setUpdatedByName(String updatedByName) {
            this.updatedByName = updatedByName;
        }

        public String getCancelledByName() {
            return cancelledByName != null ? cancelledByName : "";
        }

        public void setCancelledByName(String cancelledByName) {
            this.cancelledByName = cancelledByName;
        }

        public int getActionByUserId() {
            return actionByUserId;
        }

        public void setActionByUserId(int actionByUserId) {
            this.actionByUserId = actionByUserId;
        }

        public String getDriverInvoiceStatement() {
            return driverInvoiceStatement != null ? driverInvoiceStatement : "";
        }

        public void setDriverInvoiceStatement(String driverInvoiceStatement) {
            this.driverInvoiceStatement = driverInvoiceStatement;
        }

        public String getStatementId() {
            return statementId != null ? statementId : "";
        }

        public void setStatementId(String statementId) {
            this.statementId = statementId;
        }

        public int getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(int vehicleType) {
            this.vehicleType = vehicleType;
        }

        public int getWaitingTimeMinutes() {
            return waitingTimeMinutes;
        }

        public void setWaitingTimeMinutes(int waitingTimeMinutes) {
            this.waitingTimeMinutes = waitingTimeMinutes;
        }

        public double getWaitingTimePriceDriver() {
            return waitingTimePriceDriver;
        }

        public void setWaitingTimePriceDriver(double waitingTimePriceDriver) {
            this.waitingTimePriceDriver = waitingTimePriceDriver;
        }

        public double getWaitingTimePriceAccount() {
            return waitingTimePriceAccount;
        }

        public void setWaitingTimePriceAccount(double waitingTimePriceAccount) {
            this.waitingTimePriceAccount = waitingTimePriceAccount;
        }

        public double getParkingCharge() {
            return parkingCharge;
        }

        public void setParkingCharge(double parkingCharge) {
            this.parkingCharge = parkingCharge;
        }

        public boolean isPostedForInvoicing() {
            return postedForInvoicing;
        }

        public void setPostedForInvoicing(boolean postedForInvoicing) {
            this.postedForInvoicing = postedForInvoicing;
        }

        public boolean isPostedForStatement() {
            return postedForStatement;
        }

        public void setPostedForStatement(boolean postedForStatement) {
            this.postedForStatement = postedForStatement;
        }

        public String getAllocatedAt() {
            return allocatedAt != null ? allocatedAt : "";
        }

        public void setAllocatedAt(String allocatedAt) {
            this.allocatedAt = allocatedAt;
        }

        public int getAllocatedById() {
            return allocatedById;
        }

        public void setAllocatedById(int allocatedById) {
            this.allocatedById = allocatedById;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPaymentOrderId() {
            return paymentOrderId != null ? paymentOrderId : "";
        }

        public void setPaymentOrderId(String paymentOrderId) {
            this.paymentOrderId = paymentOrderId;
        }

        public String getPaymentLink() {
            return paymentLink != null ? paymentLink : "";
        }

        public void setPaymentLink(String paymentLink) {
            this.paymentLink = paymentLink;
        }

        public String getPaymentLinkSentBy() {
            return paymentLinkSentBy != null ? paymentLinkSentBy : "";
        }

        public void setPaymentLinkSentBy(String paymentLinkSentBy) {
            this.paymentLinkSentBy = paymentLinkSentBy;
        }

        public String getPaymentLinkSentOn() {
            return paymentLinkSentOn != null ? paymentLinkSentOn : "";
        }

        public void setPaymentLinkSentOn(String paymentLinkSentOn) {
            this.paymentLinkSentOn = paymentLinkSentOn;
        }

        public boolean isPaymentReceiptSent() {
            return paymentReceiptSent;
        }

        public void setPaymentReceiptSent(boolean paymentReceiptSent) {
            this.paymentReceiptSent = paymentReceiptSent;
        }

        public boolean isASAP() {
            return isASAP;
        }

        public void setASAP(boolean ASAP) {
            isASAP = ASAP;
        }

        public String getCellText() {
            return cellText != null ? cellText : "";
        }

        public void setCellText(String cellText) {
            this.cellText = cellText;
        }
    }
}