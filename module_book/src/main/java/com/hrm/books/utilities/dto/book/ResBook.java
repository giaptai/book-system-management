package com.hrm.books.utilities.dto.book;

public record ResBook(
        int id,
        String image,
        String title,
        double price,
        short amount,
        int yearPub
) {
}
