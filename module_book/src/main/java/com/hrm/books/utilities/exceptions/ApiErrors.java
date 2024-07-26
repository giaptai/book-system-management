package com.hrm.books.utilities.exceptions;

public record ApiErrors<T>(
        int errorCode,
        T errorMessage,
        String dateTime
) {
}
