package com.hrm.books.service;

import com.hrm.books.utilities.dto.author.ReqAuthor;
import com.hrm.books.utilities.dto.author.ResAuthor;
import com.hrm.books.utilities.dto.author.ResAuthorDetail;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;

public interface IAuthorService {
    DataResponse<ResAuthor[]> getAllHasFilter(String name, String nameOrder, String bookName, int page, int size);
    AnnounceResponse<ResAuthorDetail> getDetail(int id);
    AnnounceResponse<ResAuthor[]> add(ReqAuthor[] reqAuthor);
    AnnounceResponse<ResAuthor[]> edit(ReqAuthor[] reqAuthor);
    AnnounceResponse<Boolean> del(int[] id);
}
