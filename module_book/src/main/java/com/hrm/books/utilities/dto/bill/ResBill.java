package com.hrm.books.utilities.dto.bill;

import com.hrm.books.utilities.dto.visitor.ResVisitorAuth;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResBill(
        UUID id,
        int totalAmount,
        double totalPrice,
        String state,
        String buyerName,
        String buyerPhone,
        String deliveryAddress,
        ResVisitorAuth visitor,
        String create_at
) implements Serializable {

}
