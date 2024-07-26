package com.hrm.books.utilities.dto.category;

import com.hrm.books.models.Category;

public class CategoryMap {
    public static Category MapToCategory(ReqCategory req) {
        return new Category(req.id(), req.name());
    }

    public static ResCategory MapToResCategory(Category category) {
        return new ResCategory(category.getId(), category.getName());
    }
}
