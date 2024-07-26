package com.hrm.books.utilities.dto.book;

import java.util.List;

public class ReqBookWrapper {
    private List<ReqBook> reqBooks;

    // Getters and setters
    public List<ReqBook> getReqBooks() {
        return reqBooks;
    }

    public void setReqBooks(List<ReqBook> reqBooks) {
        this.reqBooks = reqBooks;
    }
}

