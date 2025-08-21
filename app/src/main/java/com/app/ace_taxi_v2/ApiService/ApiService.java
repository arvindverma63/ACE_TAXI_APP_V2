package com.app.ace_taxi_v2.ApiService;


import com.app.ace_taxi_v2.Components.JobStatusModal;
import com.app.ace_taxi_v2.JobModals.BookingRequest;
import com.app.ace_taxi_v2.Models.AddressIO.AddressIOLocationResponse;
import com.app.ace_taxi_v2.Models.AddressIO.AutocompleteResponse;
import com.app.ace_taxi_v2.Models.AddressIO.GetAddressLocationRequest;
import com.app.ace_taxi_v2.Models.AddressIO.PostcodeResponse;
import com.app.ace_taxi_v2.Models.AllDriverAvailabilityResponse;
import com.app.ace_taxi_v2.Models.AvailabilityRequest;
import com.app.ace_taxi_v2.Models.AvailabilityResponse;
import com.app.ace_taxi_v2.Models.BookingRequest.BookingCompleteRequest;
import com.app.ace_taxi_v2.Models.BookingRequest.BookingCompleteResponse;
import com.app.ace_taxi_v2.Models.Dashtotal;
import com.app.ace_taxi_v2.Models.DriverShiftResponse;
import com.app.ace_taxi_v2.Models.EarningResponse;
import com.app.ace_taxi_v2.Models.Expense;
import com.app.ace_taxi_v2.Models.ExpensesRequest;
import com.app.ace_taxi_v2.Models.ExpensesResponse;
import com.app.ace_taxi_v2.Models.FcmRequest;
import com.app.ace_taxi_v2.Models.FcmResponse;
import com.app.ace_taxi_v2.Models.GPSRequest;
import com.app.ace_taxi_v2.Models.GPSResponse;
import com.app.ace_taxi_v2.Models.GetActiveJobResponse;
import com.app.ace_taxi_v2.Models.ImageUploadResponse;
import com.app.ace_taxi_v2.Models.JobOfferNoticationResponse;
import com.app.ace_taxi_v2.Models.JobResponse;
import com.app.ace_taxi_v2.Models.JobStatusModel;
import com.app.ace_taxi_v2.Models.Jobs.ArrivedResponse;
import com.app.ace_taxi_v2.Models.Jobs.Booking;
import com.app.ace_taxi_v2.Models.Jobs.DateRangeList;
import com.app.ace_taxi_v2.Models.Jobs.DateRangeRequest;
import com.app.ace_taxi_v2.Models.Jobs.DateRangeResponse;
import com.app.ace_taxi_v2.Models.Jobs.FutureJobResponse;
import com.app.ace_taxi_v2.Models.Jobs.GetBookingInfo;
import com.app.ace_taxi_v2.Models.Jobs.HistoryBooking;
import com.app.ace_taxi_v2.Models.Jobs.HistoryJobResponse;
import com.app.ace_taxi_v2.Models.Jobs.TodayJobResponse;
import com.app.ace_taxi_v2.Models.Log.LogRequest;
import com.app.ace_taxi_v2.Models.LoginRequest;
import com.app.ace_taxi_v2.Models.LoginResponse;
import com.app.ace_taxi_v2.Models.POI.LocalPOIRequest;
import com.app.ace_taxi_v2.Models.POI.LocalPOIResponse;
import com.app.ace_taxi_v2.Models.QuotesRequest;
import com.app.ace_taxi_v2.Models.QuotesResponse;
import com.app.ace_taxi_v2.Models.Reports.StatementItem;
import com.app.ace_taxi_v2.Models.UserProfileResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

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
    Call<List<HistoryBooking>> historyJobs(@Header("Authorization") String token);

    @GET("/api/Bookings/FindById")
    Call<Booking> getBookingById(@Header("Authorization") String token,
                                 @Query("bookingId") int bookingId);

    @GET("/api/DriverApp/JobOfferReply")
    Call<Void> sendJobResponse(@Header("Authorization") String token,
                                      @Query("jobno") int jobId,
                                      @Query("response") int response,
                                      @Query("guid") String guid);

    @GET("/api/DriverApp/DriverShift")
    Call<Void> driverShift(@Header("Authorization") String token,
                                          @Query("userid") int userid,
                                          @Query("status") int status);

    @POST("/api/DriverApp/AddExpense")
    Call<Void> expenses(@Header("Authorization") String token,
                                    @Body ExpensesRequest expensesRequest);

    @GET("/api/DriverApp/JobStatusReply")
    Call<Void> jobStatus(@Header("Authorization") String token,
                                   @Query("jobno") int jobno,
                                   @Query("status") int status);

    @GET("/api/DriverApp/Arrived")
    Call<Void> arrivedStatusUpdate(@Header("Authorization") String token,
                                              @Query("bookingId") int bookingId);

    @GET("/api/Bookings/FindById")
    Call<GetBookingInfo> bookingInfo(@Header("Authorization") String token,
                                     @Query("bookingId") int bookingId);
    @GET("/api/DriverApp/GetExpenses")
    Call<List<Expense>> getExpenses(@Header("Authorization") String token,
                           @Query("UserId") int UserId,
                           @Query("From") String From,
                           @Query("To") String To);

    @GET("/api/DriverApp/GetStatementHeaders")
    Call<List<StatementItem>> getStatements(@Header("Authorization") String token,
                                            @Query("from") String from,
                                            @Query("to") String to,
                                            @Query("userId") int userId);

    @POST("/api/DriverApp/SetAvailability")
    Call<Void> addAvailability(@Header("Authorization") String token,
                                                     @Body AvailabilityRequest availabilityRequest);


    @POST("/api/DriverApp/DateRange")
    Call<DateRangeList> dateRangeResponse(@Header("Authorization") String token,
                                                @Body DateRangeRequest dateRangeRequest);
    @GET("/api/DriverApp/Earnings")
    Call<List<EarningResponse>> getEarningResponse(@Header("Authorization") String token,
                                                   @Query("from") String from,
                                                   @Query("to") String to);
    @Multipart
    @POST("/api/DriverApp/UploadDocument")
    Call<Void> uploadDoc(@Header("Authorization") String token,
                                        @Query("type") int type,
                                        @Part MultipartBody.Part file);

    @GET("/api/DriverApp/Availabilities")
    Call<AvailabilityResponse> getAva(@Header("Authorization") String token);

    @POST("/api/Bookings/Complete")
    Call<Void> completeBooking(@Header("Authorization") String token,
                                                  @Body BookingCompleteRequest completeRequest);

    @POST("/api/LocalPOI/GetPOI")
    Call<List<LocalPOIResponse>> autoComplete(@Header("Authorization") String token,
                                        @Body LocalPOIRequest localPOIRequest);

    @GET("/api/DriverApp/DeleteAvailability")
    Call<Void> deleteAvailablities(@Header("Authorization") String token,
                                   @Query("id") int id);

    @GET("/api/DriverApp/DashTotals")
    Call<Dashtotal> getDashTotal(@Header("Authorization") String token);

    @GET("/api/DriverApp/RetrieveJobOffer")
    Call<JobOfferNoticationResponse> getJobOffer(@Header("Authorization") String token,
                                                 @Query("guid") String guid);

    @POST("/api/DriverApp/SetActiveJob")
    Call<Void> setActiveJob(@Header("Authorization") String token,
                            @Query("bookingId") int bookingId);

    @GET("/api/DriverApp/GetActiveJob")
    Call<Integer> getActiveJob(@Header("Authorization") String token);

    @GET("/api/Accounts/DownloadStatement")
    Call<ResponseBody> downloadStatement(@Header("Authorization") String token,
                                         @Query("statementId") int statementId);

    @GET("/api/Availability/General")
    Call<List<AllDriverAvailabilityResponse>> getAllDriverAvailablity(
            @Header("Authorization") String token,
            @Query("date") String date
    );

    @POST("/api/Bookings/RankCreate")
    Call<Void> rankCreate(@Header("Authorization") String token,
                          @Body BookingRequest bookingRequest);

    @POST("/api/Bookings/GetPrice")
    Call<QuotesResponse> getQuotes(@Header("Authorization") String token,
                                   @Body QuotesRequest quotesRequest);

    @POST("autocomplete/{searchText}")
    Call<AutocompleteResponse> getAutocompleteResults(
            @retrofit2.http.Path("searchText") String searchText,
            @Query("api-key") String apiKey,
            @Body GetAddressLocationRequest getAddressLocationRequest
    );

    @GET("get/{id}")
    Call<PostcodeResponse> getPostcodeOnly(
            @retrofit2.http.Path("id") String id,
            @Query("api-key") String apiKey
    );

    @GET("nearest-location/{latitude}/{longitude}")
    Call<AddressIOLocationResponse> getAddressResponse(
            @Path("latitude") double latitude,
            @Path("longitude") double longitude,
            @Query("api-key") String apiKey
    );

    @POST("api/logs")
    Call<Void> log(@Body LogRequest logRequest);

}
