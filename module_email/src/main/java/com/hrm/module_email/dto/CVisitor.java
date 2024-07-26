package com.hrm.module_email.dto;

import java.io.Serializable;

public record CVisitor(
        String email,
        String username,
        String password
) implements Serializable {
}
