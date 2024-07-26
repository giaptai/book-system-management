package com.hrm.books.service;

import com.hrm.books.utilities.dto.book.ReqBook;
import com.hrm.books.utilities.dto.book.ResBook;
import com.hrm.books.utilities.dto.book.ResBookDetail;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBookService {
    DataResponse<List<ResBook>> getAllHasFilter(String title, String titleOrder, double priceFrom, double priceTo, short yearPub, String available, String authorName, String categoryNames, int page, int size);

    AnnounceResponse<ResBookDetail> getDetail(int id);

    List<ResBook> add(List<ReqBook> reqBooks);

    AnnounceResponse<List<ResBook>> add(MultipartFile[] file, String reqBooks);

    private List<ResBook> edit(List<ReqBook> reqBooks) {
        return null;
    }
    AnnounceResponse<List<ResBook>> edit(MultipartFile[] file, String reqBooks);

    AnnounceResponse<Boolean> del(List<Integer> ids);

    default List<ResBook> buy(int visitorId, List<ReqBook> reqBooks) {
        return null;
    }
}
