package com.hrm.books.utilities.dto.author;

import com.hrm.books.utilities.dto.book.ResBook;

import java.util.List;

public record ResAuthorDetail(
        ResAuthor author,
        List<ResBook> books
) {
}
