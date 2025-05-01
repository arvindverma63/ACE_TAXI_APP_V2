package com.app.ace_taxi_v2.Logic.Formater;

import android.content.Context;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class HHMMFormater {

    public String formatDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return "";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

                if (dateTimeStr.contains("T")) {
                    // Format: yyyy-MM-dd'T'HH:mm:ss
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, inputFormatter);
                    return dateTime.format(outputFormatter);
                } else if (dateTimeStr.contains(",")) {
                    // Format: dd MMM yyyy, hh:mm a
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.getDefault());
                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, inputFormatter);
                    return dateTime.format(outputFormatter);
                } else if (dateTimeStr.matches(".*\\d{1,2}:\\d{2} [APMapm]{2}.*")) {
                    // Format: hh:mm a (time only)
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault());
                    LocalTime time = LocalTime.parse(dateTimeStr.trim(), inputFormatter);
                    return time.format(outputFormatter);
                }
            } else {
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                if (dateTimeStr.contains("T")) {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    Date date = inputFormat.parse(dateTimeStr);
                    return outputFormat.format(date);
                } else if (dateTimeStr.contains(",")) {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                    Date date = inputFormat.parse(dateTimeStr);
                    return outputFormat.format(date);
                } else if (dateTimeStr.matches(".*\\d{1,2}:\\d{2} [APMapm]{2}.*")) {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    Date date = inputFormat.parse(dateTimeStr.trim());
                    return outputFormat.format(date);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateTimeStr;
    }




}
