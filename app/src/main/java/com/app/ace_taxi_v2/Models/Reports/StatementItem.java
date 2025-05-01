package com.app.ace_taxi_v2.Models.Reports;

import android.os.Build;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Remove: import kotlinx.coroutines.Job;

public class StatementItem {
    private int accountJobsTotalCount;
    private int cashJobsTotalCount;
    private double commissionDue;
    private String dateCreated;
    private String dateUpdated;
    private double earningsAccount;
    private double earningsCash;
    private double earningsCard;
    private int earningsRank;
    private double cardFees;
    private String startDate;
    private String endDate;
    private boolean paidInFull;
    private double paymentDue;
    private int rankJobsTotalCount;
    private int statementId;
    private double subTotal;
    private double totalEarned;
    private int totalJobCount;
    private int userId;
    private String identifier;
    private String colorCode;
//    private List<Job> jobs; // Use custom Job class instead of kotlinx.coroutines.Job

    // Existing getters and setters remain unchanged
    public int getAccountJobsTotalCount() { return accountJobsTotalCount; }
    public void setAccountJobsTotalCount(int accountJobsTotalCount) { this.accountJobsTotalCount = accountJobsTotalCount; }
    public int getCashJobsTotalCount() { return cashJobsTotalCount; }
    public void setCashJobsTotalCount(int cashJobsTotalCount) { this.cashJobsTotalCount = cashJobsTotalCount; }
    public double getCommissionDue() { return commissionDue; }
    public void setCommissionDue(double commissionDue) { this.commissionDue = commissionDue; }

    public String getDateCreated() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime dateTime = LocalDateTime.parse(dateCreated);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
                return dateTime.format(formatter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return dateCreated;
        }
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }
    public String getDateUpdated() { return dateUpdated; }
    public void setDateUpdated(String dateUpdated) { this.dateUpdated = dateUpdated; }
    public double getEarningsAccount() { return earningsAccount; }
    public void setEarningsAccount(double earningsAccount) { this.earningsAccount = earningsAccount; }
    public double getEarningsCash() { return earningsCash; }
    public void setEarningsCash(double earningsCash) { this.earningsCash = earningsCash; }
    public double getEarningsCard() { return earningsCard; }
    public void setEarningsCard(double earningsCard) { this.earningsCard = earningsCard; }
    public int getEarningsRank() { return earningsRank; }
    public void setEarningsRank(int earningsRank) { this.earningsRank = earningsRank; }
    public double getCardFees() { return cardFees; }
    public void setCardFees(double cardFees) { this.cardFees = cardFees; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public boolean isPaidInFull() { return paidInFull; }
    public void setPaidInFull(boolean paidInFull) { this.paidInFull = paidInFull; }
    public double getPaymentDue() { return paymentDue; }
    public void setPaymentDue(double paymentDue) { this.paymentDue = paymentDue; }
    public int getRankJobsTotalCount() { return rankJobsTotalCount; }
    public void setRankJobsTotalCount(int rankJobsTotalCount) { this.rankJobsTotalCount = rankJobsTotalCount; }
    public int getStatementId() { return statementId; }
    public void setStatementId(int statementId) { this.statementId = statementId; }
    public double getSubTotal() { return subTotal; }
    public void setSubTotal(double subTotal) { this.subTotal = subTotal; }
    public double getTotalEarned() { return totalEarned; }
    public void setTotalEarned(double totalEarned) { this.totalEarned = totalEarned; }
    public int getTotalJobCount() { return totalJobCount; }
    public void setTotalJobCount(int totalJobCount) { this.totalJobCount = totalJobCount; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }
//    public List<Job> getJobs() { return jobs; }
//    public void setJobs(List<Job> jobs) { this.jobs = jobs; }
}

// Define the Job class to match your JSON structure
class Job {
    private String accNo;
    private int bookingId;
    private int userId;
    private String date;
    private int passengers;
    private String pickup;
    private String pickupPostcode;
    private String destination;
    private String destinationPostcode;
    private List<String> vias;
    private boolean hasVias;
    private String passenger;
    private double price;
    private int scope;
    private boolean cancelled;
    private boolean coa;
    private int vehicleType;
    private double priceAccount;
    private String details;
    private boolean hasDetails;
    private int waitingMinutes;
    private String paymentStatus;
    private String waitingTime;
    private double waitingPriceDriver;
    private double waitingPriceAccount;
    private double parkingCharge;
    private double totalCharge;
    private double totalCost;
    private boolean postedForInvoicing;
    private boolean postedForStatement;
    private int miles;

    // Default constructor (required for Gson)
    public Job() {}

    // Getters and Setters
    public String getAccNo() { return accNo; }
    public void setAccNo(String accNo) { this.accNo = accNo; }
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getPassengers() { return passengers; }
    public void setPassengers(int passengers) { this.passengers = passengers; }
    public String getPickup() { return pickup; }
    public void setPickup(String pickup) { this.pickup = pickup; }
    public String getPickupPostcode() { return pickupPostcode; }
    public void setPickupPostcode(String pickupPostcode) { this.pickupPostcode = pickupPostcode; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getDestinationPostcode() { return destinationPostcode; }
    public void setDestinationPostcode(String destinationPostcode) { this.destinationPostcode = destinationPostcode; }
    public List<String> getVias() { return vias; }
    public void setVias(List<String> vias) { this.vias = vias; }
    public boolean isHasVias() { return hasVias; }
    public void setHasVias(boolean hasVias) { this.hasVias = hasVias; }
    public String getPassenger() { return passenger; }
    public void setPassenger(String passenger) { this.passenger = passenger; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getScope() { return scope; }
    public void setScope(int scope) { this.scope = scope; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public boolean isCoa() { return coa; }
    public void setCoa(boolean coa) { this.coa = coa; }
    public int getVehicleType() { return vehicleType; }
    public void setVehicleType(int vehicleType) { this.vehicleType = vehicleType; }
    public double getPriceAccount() { return priceAccount; }
    public void setPriceAccount(double priceAccount) { this.priceAccount = priceAccount; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public boolean isHasDetails() { return hasDetails; }
    public void setHasDetails(boolean hasDetails) { this.hasDetails = hasDetails; }
    public int getWaitingMinutes() { return waitingMinutes; }
    public void setWaitingMinutes(int waitingMinutes) { this.waitingMinutes = waitingMinutes; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getWaitingTime() { return waitingTime; }
    public void setWaitingTime(String waitingTime) { this.waitingTime = waitingTime; }
    public double getWaitingPriceDriver() { return waitingPriceDriver; }
    public void setWaitingPriceDriver(double waitingPriceDriver) { this.waitingPriceDriver = waitingPriceDriver; }
    public double getWaitingPriceAccount() { return waitingPriceAccount; }
    public void setWaitingPriceAccount(double waitingPriceAccount) { this.waitingPriceAccount = waitingPriceAccount; }
    public double getParkingCharge() { return parkingCharge; }
    public void setParkingCharge(double parkingCharge) { this.parkingCharge = parkingCharge; }
    public double getTotalCharge() { return totalCharge; }
    public void setTotalCharge(double totalCharge) { this.totalCharge = totalCharge; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public boolean isPostedForInvoicing() { return postedForInvoicing; }
    public void setPostedForInvoicing(boolean postedForInvoicing) { this.postedForInvoicing = postedForInvoicing; }
    public boolean isPostedForStatement() { return postedForStatement; }
    public void setPostedForStatement(boolean postedForStatement) { this.postedForStatement = postedForStatement; }
    public int getMiles() { return miles; }
    public void setMiles(int miles) { this.miles = miles; }
}