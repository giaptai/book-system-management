package com.hrm.books.controller;

import com.hrm.books.service.IVisitorService;
import com.hrm.books.utilities.enums.Role;
import com.hrm.books.utilities.dto.visitor.Login;
import com.hrm.books.utilities.dto.visitor.ReqVisitor;
import com.hrm.books.utilities.dto.visitor.ResLogin;
import com.hrm.books.utilities.dto.visitor.ResVisitor;
import com.hrm.books.utilities.dto.visitor.ResVisitorDetail;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VisitorController {
    final IVisitorService visitorService;

    @PostMapping(path = "/login")
    public ResponseEntity<ResLogin> login(@RequestBody Login login) {
        return new ResponseEntity<>(visitorService.login(login), HttpStatus.OK);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ResVisitor> register(@RequestBody ReqVisitor reqVisitor) {
        return new ResponseEntity<>(visitorService.register(reqVisitor), HttpStatus.CREATED);
    }

    @GetMapping(path = "/visitor")
    public ResponseEntity<DataResponse<ResVisitor[]>> getAllHasFilter(@RequestParam(required = false) String email,
                                                                     @RequestParam(required = false) String username,
                                                                     @RequestParam(required = false) Role[] role,
                                                                     @RequestParam(defaultValue = "1") int page,
                                                                     @RequestParam(defaultValue = "15") int size) {
        return new ResponseEntity<>(visitorService.getAllHasFilter(email, username, role, page, size), HttpStatus.OK);
    }

    @GetMapping(path = "/visitor/{id}")
    public ResponseEntity<AnnounceResponse<ResVisitorDetail>> getDetail(@PathVariable(name = "id") int id) {
        return new ResponseEntity<>(visitorService.getDetail(id), HttpStatus.OK);
    }

//    @PostMapping(path = "/visitor")
//    public ResponseEntity<ResVisitor[]> add(@RequestBody ReqVisitor[] reqVisitors) {
//        return new ResponseEntity<>(visitorService.add(reqVisitors), HttpStatus.CREATED);
//    }

//    @PutMapping(path = "/visitor/{id}")
//    public ResponseEntity<ResVisitor> edit(@PathVariable(name = "id") int id, @RequestBody ReqVisitor reqVisitor) {
//        return new ResponseEntity<>(visitorService.edit(id, reqVisitor), HttpStatus.OK);
//    }

//    @DeleteMapping(path = "/visitor/{id}")
//    public ResponseEntity<Boolean> del(@PathVariable(name = "id") int id) {
//        return new ResponseEntity<>(visitorService.del(id), HttpStatus.OK);
//    }
}
