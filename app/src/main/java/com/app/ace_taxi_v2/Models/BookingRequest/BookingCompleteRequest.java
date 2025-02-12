package com.app.ace_taxi_v2.Models.BookingRequest;

public class BookingCompleteRequest {

    private int bookingId;
    private int waitingTime;
    private int parkingCharge;
    private double driverPrice;
    private double accountPrice;

    // Default Constructor
    public BookingCompleteRequest() {
    }

    // Parameterized Constructor
    public BookingCompleteRequest(int bookingId, int waitingTime, int parkingCharge, double driverPrice, double accountPrice) {
        this.bookingId = bookingId;
        this.waitingTime = waitingTime;
        this.parkingCharge = parkingCharge;
        this.driverPrice = driverPrice;
        this.accountPrice = accountPrice;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getParkingCharge() {
        return parkingCharge;
    }

    public void setParkingCharge(int parkingCharge) {
        this.parkingCharge = parkingCharge;
    }

    public double getDriverPrice() {
        return driverPrice;
    }

    public void setDriverPrice(double driverPrice) {
        this.driverPrice = driverPrice;
    }

    public double getAccountPrice() {
        return accountPrice;
    }

    public void setAccountPrice(double accountPrice) {
        this.accountPrice = accountPrice;
    }

    @Override
    public String toString() {
        return "BookingCompleteRequest{" +
                "bookingId=" + bookingId +
                ", waitingTime=" + waitingTime +
                ", parkingCharge=" + parkingCharge +
                ", driverPrice=" + driverPrice +
                ", accountPrice=" + accountPrice +
                '}';
    }
}
