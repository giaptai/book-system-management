package com.hrm.books.utilities.responses;

import java.time.LocalDateTime;

import static com.hrm.books.utilities.constants.Constants.DATE_TIME_FORMATTER_DASH;

public class AnnounceResponse<T> {
    private int statusCode;
    private T data;
    private String timestamp;

    public AnnounceResponse() {
    }

    public AnnounceResponse(int statusCode, T data) {
        this.statusCode = statusCode;
        this.data = data;
        this.timestamp = DATE_TIME_FORMATTER_DASH.format(LocalDateTime.now());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
