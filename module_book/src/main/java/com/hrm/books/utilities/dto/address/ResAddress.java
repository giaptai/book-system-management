package com.hrm.books.utilities.dto.address;

public record ResAddress(
        int id,
        String nameReceive,
        String phoneReceive,
        String location,
        String username
) {
}
