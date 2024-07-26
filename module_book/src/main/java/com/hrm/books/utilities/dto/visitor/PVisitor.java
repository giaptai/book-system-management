package com.hrm.books.utilities.dto.visitor;

import java.io.Serializable;

public record PVisitor(
        String email,
        String username,
        String password
) implements Serializable{
}
