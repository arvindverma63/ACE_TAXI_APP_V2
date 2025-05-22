package com.app.ace_taxi_v2.Logic.Formater;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String formateTimeStampToDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return "";

        // Fix invalid month (e.g., 2025-0-06 to 2025-01-06)
        String correctedDateTimeStr = dateTimeStr;
        if (correctedDateTimeStr.contains("-0-")) {
            correctedDateTimeStr = correctedDateTimeStr.replace("-0-", "-01-");
        } else if (correctedDateTimeStr.matches(".*-0\\d-.*")) {
            correctedDateTimeStr = correctedDateTimeStr.replaceAll("-0(\\d)-", "-01-");
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Define output formatter
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

                if (correctedDateTimeStr.contains("T")) {
                    // Handle ISO 8601 format: yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS]
                    String[] parts = correctedDateTimeStr.split("\\.");
                    String mainPart = parts[0]; // yyyy-MM-dd'T'HH:mm:ss
                    String nanoPart = parts.length > 1 ? parts[1].replaceAll("[^0-9]", "") : "";
                    // Limit to 9 digits for nanoseconds
                    nanoPart = nanoPart.length() > 9 ? nanoPart.substring(0, 9) : nanoPart;
                    String normalizedDateTime = nanoPart.isEmpty() ? mainPart : mainPart + "." + nanoPart;

                    // Create a flexible formatter for variable nanosecond precision
                    DateTimeFormatter inputFormatter = new DateTimeFormatterBuilder()
                            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                            .optionalStart()
                            .appendFraction(java.time.temporal.ChronoField.NANO_OF_SECOND, 0, 9, true)
                            .optionalEnd()
                            .toFormatter(Locale.getDefault());

                    LocalDateTime dateTime = LocalDateTime.parse(normalizedDateTime, inputFormatter);
                    return dateTime.format(outputFormatter);
                }
            } else {
                // Legacy handling for older Android versions
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                if (correctedDateTimeStr.contains("T")) {
                    // Handle: yyyy-MM-dd'T'HH:mm:ss[.SSS]
                    String[] parts = correctedDateTimeStr.split("\\.");
                    String mainPart = parts[0]; // yyyy-MM-dd'T'HH:mm:ss
                    String milliPart = parts.length > 1 ? parts[1].replaceAll("[^0-9]", "") : "";
                    // Limit to 3 digits for milliseconds
                    milliPart = milliPart.length() > 3 ? milliPart.substring(0, 3) : milliPart;
                    String normalizedDateTime = milliPart.isEmpty() ? mainPart : mainPart + "." + milliPart;

                    SimpleDateFormat inputFormat = new SimpleDateFormat(
                            milliPart.isEmpty() ? "yyyy-MM-dd'T'HH:mm:ss" : "yyyy-MM-dd'T'HH:mm:ss.SSS",
                            Locale.getDefault()
                    );
                    Date date = inputFormat.parse(normalizedDateTime);
                    return outputFormat.format(date);
                }
            }
        } catch (DateTimeParseException | java.text.ParseException e) {
            e.printStackTrace();
        }

        return correctedDateTimeStr; // Return corrected input on error
    }

    public String formateToEEDD(String dateTimeStr) {
        if (dateTimeStr == null) return "";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EE dd/MM", Locale.getDefault());

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
                SimpleDateFormat outputFormat = new SimpleDateFormat("EE dd/MM", Locale.getDefault());

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
