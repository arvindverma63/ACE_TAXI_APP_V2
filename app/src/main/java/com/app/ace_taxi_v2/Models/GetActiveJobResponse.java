package com.app.ace_taxi_v2.Models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class GetActiveJobResponse implements Parcelable {

    @SerializedName("bookingId")
    private final int bookingId;

    public GetActiveJobResponse() {
        this.bookingId = 0;
    }

    public GetActiveJobResponse(int bookingId) {
        if (bookingId < 0) {
            throw new IllegalArgumentException("Booking ID cannot be negative: " + bookingId);
        }
        this.bookingId = bookingId;
    }

    public int getBookingId() {
        return bookingId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetActiveJobResponse that = (GetActiveJobResponse) o;
        return bookingId == that.bookingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    @NonNull
    public String toString() {
        return "GetActiveJobResponse{bookingId=" + bookingId + '}';
    }

    // Parcelable implementation
    protected GetActiveJobResponse(@NonNull Parcel in) {
        bookingId = in.readInt();
    }

    public static final Creator<GetActiveJobResponse> CREATOR = new Creator<GetActiveJobResponse>() {
        @Override
        public GetActiveJobResponse createFromParcel(Parcel in) {
            return new GetActiveJobResponse(in);
        }

        @Override
        public GetActiveJobResponse[] newArray(int size) {
            return new GetActiveJobResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(bookingId);
    }
}