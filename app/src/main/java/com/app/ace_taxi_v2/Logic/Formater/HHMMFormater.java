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

    public String formateTimeStampToDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return "";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault());

                if (dateTimeStr.contains("T")) {
                    if (dateTimeStr.contains(".")) {
                        // Handle format: yyyy-MM-dd'T'HH:mm:ss.SSSSSSS (or similar with more nanoseconds)
                        // Trim to nanoseconds that Java supports (max 9 digits)
                        String trimmed = dateTimeStr;
                        int dotIndex = dateTimeStr.indexOf(".");
                        if (dotIndex != -1) {
                            String beforeDot = dateTimeStr.substring(0, dotIndex);
                            String afterDot = dateTimeStr.substring(dotIndex + 1).replaceAll("[^0-9]", "");
                            afterDot = afterDot.substring(0, Math.min(afterDot.length(), 9)); // trim to 9 digits max
                            trimmed = beforeDot + "." + afterDot;
                            // Add 'Z' to satisfy ISO if not present (optional)
                        }
                        LocalDateTime dateTime = LocalDateTime.parse(trimmed, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault()));
                        return dateTime.format(outputFormatter);
                    } else {
                        // Format: yyyy-MM-dd'T'HH:mm:ss
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, inputFormatter);
                        return dateTime.format(outputFormatter);
                    }
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
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                if (dateTimeStr.contains("T")) {
                    if (dateTimeStr.contains(".")) {
                        // Handle: yyyy-MM-dd'T'HH:mm:ss.SSSSSSS
                        // Truncate to milliseconds (3 digits after dot)
                        int dotIndex = dateTimeStr.indexOf(".");
                        int endIndex = dotIndex + 4; // Include milliseconds only
                        if (dotIndex != -1 && dateTimeStr.length() > endIndex) {
                            dateTimeStr = dateTimeStr.substring(0, endIndex);
                        }
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                        Date date = inputFormat.parse(dateTimeStr);
                        return outputFormat.format(date);
                    } else {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        Date date = inputFormat.parse(dateTimeStr);
                        return outputFormat.format(date);
                    }
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
