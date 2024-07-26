package com.hrm.books.utilities.dto.book;

import com.hrm.books.models.Author;
import com.hrm.books.models.Book;
import com.hrm.books.models.Category;
import com.hrm.books.utilities.dto.author.AuthorMap;
import com.hrm.books.utilities.dto.author.ReqAuthor;
import com.hrm.books.utilities.dto.author.ResAuthor;
import com.hrm.books.utilities.dto.category.CategoryMap;
import com.hrm.books.utilities.dto.category.ReqCategory;
import com.hrm.books.utilities.dto.category.ResCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

public class BookMap {
    public static Book MapToBookFile(MultipartFile file, ReqBook req) {
        Set<Author> authorSet = new HashSet<>();
        Set<Category> categorySet = new HashSet<>();
        for (ReqAuthor reqAuthor : req.authors()) {
            Author author = new Author(reqAuthor.id(), reqAuthor.name());
            authorSet.add(author);
        }
        for (ReqCategory reqCategory : req.categories()) {
            Category category = new Category(reqCategory.id(), reqCategory.name());
            categorySet.add(category);
        }
        return req.id() == 0 ?
                new Book(
                        file != null ? file.getOriginalFilename() : "",
                        req.title(),
                        req.price(),
                        req.amount(),
                        req.yearPub(),
                        authorSet,
                        categorySet
                ) : new Book(
                req.id(),
                file != null ? file.getOriginalFilename() : "",
                req.title(),
                req.price(),
                req.amount(),
                req.yearPub(),
                authorSet,
                categorySet
        );
    }

//    public static Book MapToBook(ReqBook req) {
//        return new Book(
//                "req.image().getOriginalFilename()",
//                req.title(),
//                req.price(),
//                req.amount(),
//                req.yearPub()
//        );
//    }

    public static ResBook MapToResBook(Book book) {
        return new ResBook(
                book.getId(),
                book.getImage(),
                book.getTitle(),
                book.getPrice(),
                book.getAmount(),
                book.getYearPub()
        );
    }

    public static ResBookDetail MapToResBookDetail(Book book) {
        return new ResBookDetail(
                book.getId(),
                book.getImage(),
                book.getTitle(),
                book.getPrice(),
                book.getAmount(),
                book.getAuthors().stream().map(AuthorMap::MapToResAuthor).toArray(ResAuthor[]::new),
                book.getCategories().stream().map(CategoryMap::MapToResCategory).toArray(ResCategory[]::new)
        );
    }
}
