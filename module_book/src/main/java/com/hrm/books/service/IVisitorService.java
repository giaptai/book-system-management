package com.hrm.books.service;

import com.hrm.books.utilities.enums.Role;
import com.hrm.books.utilities.dto.visitor.Login;
import com.hrm.books.utilities.dto.visitor.ReqVisitor;
import com.hrm.books.utilities.dto.visitor.ResLogin;
import com.hrm.books.utilities.dto.visitor.ResVisitor;
import com.hrm.books.utilities.dto.visitor.ResVisitorDetail;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;

public interface IVisitorService {
    ResLogin login(Login login);

    ResVisitor register(ReqVisitor reqVisitor);

    DataResponse<ResVisitor[]> getAllHasFilter(String email, String username, Role[] role, int page, int size);

    AnnounceResponse<ResVisitorDetail> getDetail(int id);

    ResVisitor[] add(ReqVisitor[] reqVisitors);

    ResVisitor[] edit(ReqVisitor[] reqVisitors);

    ResVisitor edit(ReqVisitor reqVisitors);

    ResVisitor edit(int id, ReqVisitor reqVisitors);

    AnnounceResponse<Boolean> del(int id);
}
