package com.hrm.books.utilities.dto.book;

import com.hrm.books.utilities.dto.author.ReqAuthor;
import com.hrm.books.utilities.dto.category.ReqCategory;

public record ReqBook(
        int id,
        String title,
        double price,
        short amount,
        short yearPub,
        ReqAuthor[] authors,
        ReqCategory[] categories
) {
}
