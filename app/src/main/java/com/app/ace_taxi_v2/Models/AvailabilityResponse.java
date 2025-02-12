package com.app.ace_taxi_v2.Models;

import java.util.List;

public class AvailabilityResponse {


    public List<Driver> drivers;

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public static class Driver{
        private int id;
        private int userId;
        private String fullName;
        private String date;
        private String description;
        private String colorCode;
        private int availabilityType;
        private String from;
        private String to;
        private boolean giveOrTake;
        private String availableHours;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getColorCode() {
            return colorCode;
        }

        public void setColorCode(String colorCode) {
            this.colorCode = colorCode;
        }

        public int getAvailabilityType() {
            return availabilityType;
        }

        public void setAvailabilityType(int availabilityType) {
            this.availabilityType = availabilityType;
        }

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

        public boolean isGiveOrTake() {
            return giveOrTake;
        }

        public void setGiveOrTake(boolean giveOrTake) {
            this.giveOrTake = giveOrTake;
        }

        public String getAvailableHours() {
            return availableHours;
        }

        public void setAvailableHours(String availableHours) {
            this.availableHours = availableHours;
        }
    }

}
