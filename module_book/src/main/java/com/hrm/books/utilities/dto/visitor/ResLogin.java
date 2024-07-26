package com.hrm.books.utilities.dto.visitor;

public record ResLogin(
        String username,
        String role,
        String token
) {
}
