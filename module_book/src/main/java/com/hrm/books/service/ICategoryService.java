package com.hrm.books.service;

import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;
import com.hrm.books.utilities.dto.category.ReqCategory;
import com.hrm.books.utilities.dto.category.ResCategory;

public interface ICategoryService {
    DataResponse<ResCategory[]> getAllHasFilter(String name, String nameOrder, int page, int size);

    AnnounceResponse<ResCategory[]> add(ReqCategory[] reqCategories);

    AnnounceResponse<ResCategory[]> edit(ReqCategory[] reqCategories);

    AnnounceResponse<Boolean> del(int[] id);
}
