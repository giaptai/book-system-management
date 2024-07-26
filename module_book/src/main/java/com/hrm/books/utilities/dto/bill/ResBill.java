package com.hrm.books.utilities.dto.bill;

import com.hrm.books.utilities.dto.visitor.ResVisitorAuth;

import java.io.Serializable;
import java.util.UUID;

public record ResBill(
        UUID id,
        int totalAmount,
        double totalPrice,
        String state,
        String buyerName,
        String buyerPhone,
        String deliveryAddress,
        ResVisitorAuth visitor
) implements Serializable {

}
