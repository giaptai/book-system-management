package com.hrm.books.utilities.dto.bill;

import com.hrm.books.utilities.dto.book.ReqBookBuy;

import java.util.List;

public record ReqBill(
        int addressId,
        List<ReqBookBuy> books
) {
}
