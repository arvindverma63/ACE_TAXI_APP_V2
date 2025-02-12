package com.app.ace_taxi_v2.Models;
public class GPSRequest {
    public int userId;
    public double latitude;
    public double longtitude;
    public double heading;
    public double speed;

    public GPSRequest(int userId,double latitude,double longtitude,double heading,double speed){
        this.userId = userId;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.heading = heading;
        this.speed = speed;
    }
}
