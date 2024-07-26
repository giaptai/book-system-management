package com.hrm.books.utilities.dto.visitor;

import com.hrm.books.utilities.enums.Role;

public record ReqVisitor(
        String name,
        String email,
        String password,
        Role role
) {
}
