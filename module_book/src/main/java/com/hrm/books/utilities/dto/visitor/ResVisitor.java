package com.hrm.books.utilities.dto.visitor;

import com.hrm.books.utilities.enums.Role;

public record ResVisitor(
        int id,
        String name,
        String email,
        String username,
        Role role
) {
}
