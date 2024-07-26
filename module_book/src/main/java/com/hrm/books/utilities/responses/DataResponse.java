package com.hrm.books.utilities.responses;

import java.time.LocalDateTime;

import static com.hrm.books.utilities.constants.Constants.DATE_TIME_FORMATTER_DASH;

public class DataResponse<T> {
    private short statusCode;
    private T data;
    private String timestamp;
    private long totalSize = 15;
    private int currentPage = 1;
    private long totalPage;

    public DataResponse() {
    }

    public DataResponse(short statusCode, T data, long totalSize, int currentPage, long totalPage) {
        this.statusCode = statusCode;
        this.data = data;
        this.timestamp = DATE_TIME_FORMATTER_DASH.format(LocalDateTime.now());
        this.totalSize = totalSize;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
    }

    public short getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(short statusCode) {
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

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long size) {
        this.totalSize = size;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }
}
