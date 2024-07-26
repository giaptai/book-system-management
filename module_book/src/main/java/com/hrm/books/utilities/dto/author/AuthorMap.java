package com.hrm.books.utilities.dto.author;

import com.hrm.books.models.Author;
import com.hrm.books.utilities.dto.book.BookMap;

public class AuthorMap {
    public static Author MapToAuthor(ReqAuthor req) {
        return new Author(req.name());
    }

    public static ResAuthor MapToResAuthor(Author author) {
        return new ResAuthor(author.getId(), author.getName());
    }

    public static ResAuthorDetail MapToResAuthorDetail(Author author) {
        return new ResAuthorDetail(
                MapToResAuthor(author),
                author.getBooks().stream().map(BookMap::MapToResBook).toList()
        );
    }

}
