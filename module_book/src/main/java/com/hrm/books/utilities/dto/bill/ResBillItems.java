package com.hrm.books.utilities.dto.bill;

public record ResBillItems(
        int id,
        String title,
        double price,
        short amount,
        double totalCost
) {
}
