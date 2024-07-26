package com.hrm.books.utilities.dto.bill;

import java.util.List;

public record ResBillDetail(
        ResBill bill,
        List<ResBillItems> billItems
) {
}
