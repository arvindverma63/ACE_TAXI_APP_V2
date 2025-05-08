package com.app.ace_taxi_v2.Models;

import java.util.List;

public class AllDriverAvailabilityResponse {
    private Long userId;
    private String fullName;
    private String date; // Changed from LocalDateTime to String
    private String colorCode;
    private Integer vehicleType;
    private List<AvailabilityHour> availableHours;
    private List<AvailabilityHour> unAvailableHours;
    private List<AvailabilityHour> allocatedHours;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Integer getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Integer vehicleType) {
        this.vehicleType = vehicleType;
    }

    public List<AvailabilityHour> getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(List<AvailabilityHour> availableHours) {
        this.availableHours = availableHours;
    }

    public List<AvailabilityHour> getUnAvailableHours() {
        return unAvailableHours;
    }

    public void setUnAvailableHours(List<AvailabilityHour> unAvailableHours) {
        this.unAvailableHours = unAvailableHours;
    }

    public List<AvailabilityHour> getAllocatedHours() {
        return allocatedHours;
    }

    public void setAllocatedHours(List<AvailabilityHour> allocatedHours) {
        this.allocatedHours = allocatedHours;
    }

    public static class AvailabilityHour {
        private String from;
        private String to;
        private String note;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
}