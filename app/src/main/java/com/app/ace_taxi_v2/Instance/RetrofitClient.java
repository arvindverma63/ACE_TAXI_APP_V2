package com.app.ace_taxi_v2.Instance;

import android.util.Log;
import com.app.ace_taxi_v2.Helper.DeviceMode;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    // Always read the latest base URL from DeviceMode
    public static Retrofit getInstance() {
        if (retrofit == null) {
            createRetrofitInstance();
        }
        return retrofit;
    }

    // Create a new Retrofit instance with the latest base URL
    private static void createRetrofitInstance() {
        try {
            String baseUrl = DeviceMode.getBaseURLStatic();
            Log.e("Retrofit Base URL", baseUrl);
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clear the current Retrofit instance (use on mode change)
    public static void resetRetrofitInstance() {
        retrofit = null;
    }
}
