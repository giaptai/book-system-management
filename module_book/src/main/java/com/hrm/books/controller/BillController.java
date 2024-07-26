package com.hrm.books.controller;

import com.hrm.books.service.IBillService;
import com.hrm.books.utilities.enums.State;
import com.hrm.books.utilities.dto.bill.ResBill;
import com.hrm.books.utilities.dto.bill.ResBillDetail;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillController {
    final IBillService billService;

    @GetMapping(value = "/bill")
    public ResponseEntity<DataResponse<List<ResBill>>> getBills(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) State state,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        return new ResponseEntity<>(billService.getAllHasFilter(username, state, page, size), HttpStatus.OK);
    }

    @GetMapping(value = "/bill/{id}")
    public ResponseEntity<AnnounceResponse<ResBillDetail>> getBillById(@PathVariable(name = "id") UUID id) {
        return new ResponseEntity<>(billService.getDetails(id), HttpStatus.OK);
    }

    @PatchMapping(value = "/bill")
    public ResponseEntity<AnnounceResponse<List<ResBill>>> getBillById(@RequestBody List<UUID> uuids, @RequestParam(defaultValue = "RETURNED") State state) {
        return new ResponseEntity<>(billService.updateState(uuids, state), HttpStatus.ACCEPTED);
    }

//    @PostMapping(value = "/buy/{visitorId}")
//    public ResponseEntity<ResBill> buy(@PathVariable(name = "visitorId") int visitorId, @RequestBody List<ReqBook> req) {
//        return new ResponseEntity<>(billService.create(visitorId, req), HttpStatus.OK);
//    }
}
