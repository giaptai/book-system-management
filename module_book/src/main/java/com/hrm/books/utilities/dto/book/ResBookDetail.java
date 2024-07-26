package com.hrm.books.utilities.dto.book;

import com.hrm.books.utilities.dto.author.ResAuthor;
import com.hrm.books.utilities.dto.category.ResCategory;

public record ResBookDetail(
        int id,
        String image,
        String title,
        double price,
        short amount,
        ResAuthor[] authors,
        ResCategory[] categories
) {
}
