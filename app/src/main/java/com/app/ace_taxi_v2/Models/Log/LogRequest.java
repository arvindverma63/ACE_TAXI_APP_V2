package com.app.ace_taxi_v2.Models.Log;

public class LogRequest {
    public String type;
    public String message;
    public String source;

    public LogRequest(String type, String message, String source) {
        this.type = type;
        this.message = message;
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
