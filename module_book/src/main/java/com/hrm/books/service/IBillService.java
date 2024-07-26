package com.hrm.books.service;

import com.hrm.books.utilities.enums.State;
import com.hrm.books.utilities.dto.bill.ReqBill;
import com.hrm.books.utilities.dto.bill.ResBill;
import com.hrm.books.utilities.dto.bill.ResBillDetail;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;

import java.util.List;
import java.util.UUID;

public interface IBillService {
    DataResponse<List<ResBill>> getAllHasFilter(String username, State state, int page, int size);

    AnnounceResponse<List<ResBill>> updateState(List<UUID> uuids, State state);

    ResBill create(ReqBill reqBooks);

    boolean del(List<UUID> uuids);

    AnnounceResponse<ResBillDetail> getDetails(UUID id);
}
