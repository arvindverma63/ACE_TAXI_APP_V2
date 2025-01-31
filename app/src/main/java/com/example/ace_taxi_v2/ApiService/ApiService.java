package com.example.ace_taxi_v2.ApiService;


import com.example.ace_taxi_v2.Models.DriverShiftResponse;
import com.example.ace_taxi_v2.Models.ExpensesRequest;
import com.example.ace_taxi_v2.Models.ExpensesResponse;
import com.example.ace_taxi_v2.Models.FcmRequest;
import com.example.ace_taxi_v2.Models.FcmResponse;
import com.example.ace_taxi_v2.Models.GPSRequest;
import com.example.ace_taxi_v2.Models.GPSResponse;
import com.example.ace_taxi_v2.Models.JobResponse;
import com.example.ace_taxi_v2.Models.Jobs.Booking;
import com.example.ace_taxi_v2.Models.Jobs.FutureJobResponse;
import com.example.ace_taxi_v2.Models.Jobs.HistoryJobResponse;
import com.example.ace_taxi_v2.Models.Jobs.TodayJobResponse;
import com.example.ace_taxi_v2.Models.LoginRequest;
import com.example.ace_taxi_v2.Models.LoginResponse;
import com.example.ace_taxi_v2.Models.UserProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/Auth/Authenticate")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("/api/DriverApp/UpdateGPS")
    Call<GPSResponse> updateGps(
            @Header("Authorization") String token, // Pass the Bearer token for authentication
            @Body GPSRequest gpsRequest
    );

    @POST("/api/DriverApp/UpdateFCM")
    Call<FcmResponse> updateFcm(
            @Header("Authorization") String token,
            @Body FcmRequest fcmRequest
    );

    @GET("/api/DriverApp/GetProfile")
    Call<UserProfileResponse> userProfile(@Query("username")String username,
                                          @Header("Authorization") String token);

    @GET("/api/DriverApp/FutureJobs")
    Call<FutureJobResponse> futureJobs(@Header("Authorization") String token);

    @GET("/api/DriverApp/TodaysJobs")
    Call<TodayJobResponse> todayJobs(@Header("Authorization") String token);

    @GET("/api/DriverApp/CompletedJobs")
    Call<HistoryJobResponse> historyJobs(@Header("Authorization") String token);

    @GET("/api/Bookings/FindById")
    Call<Booking> getBookingById(@Header("Authorization") String token,
                                 @Query("bookingId") int bookingId);

    @GET("/api/DriverApp/JobOfferReply")
    Call<JobResponse> sendJobResponse(@Header("Authorization") String token,
                                      @Query("jobno") int jobId,
                                      @Query("response") int response);

    @GET("/api/DriverApp/DriverShift")
    Call<DriverShiftResponse> driverShift(@Header("Authorization") String token,
                                          @Query("userid") int userid,
                                          @Query("status") int status);

    @POST("/api/DriverApp/AddExpense")
    Call<ExpensesResponse> expenses(@Header("Authorization") String token,
                                    @Body ExpensesRequest expensesRequest);

}
