package com.hrm.books.controller;

import com.hrm.books.service.IBillService;
import com.hrm.books.utilities.dto.bill.ReqBill;
import com.hrm.books.utilities.dto.bill.ResBill;
import com.hrm.books.utilities.dto.book.ResBook;
import com.hrm.books.service.IBookService;
import com.hrm.books.utilities.dto.book.ResBookDetail;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookController {
    final IBookService bookService;
    final IBillService billService;

    @Operation(
            parameters = @Parameter(
                    in = ParameterIn.QUERY,
                    name = "available",
                    schema = @Schema(allowableValues = {"Y", "N"})
            )
    )
    @GetMapping(value = "/book")
    public ResponseEntity<DataResponse<List<ResBook>>> getAllHasFilter(
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "ASC") String titleOrder,
            @RequestParam(required = false, defaultValue = "0.0") double priceFrom,
            @RequestParam(required = false, defaultValue = "9999999999.0") double priceTo,
            @RequestParam(required = false, defaultValue = "0") short yearPub,
            @RequestParam(required = false, name = "available") String available,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) String categoryNames,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "15") int size
    ) {
        return new ResponseEntity<>(bookService.getAllHasFilter(title, titleOrder, priceFrom, priceTo, yearPub, available, authorName, categoryNames, page, size), HttpStatus.OK);
    }

    @GetMapping(value = "/book/{id}")
    public ResponseEntity<AnnounceResponse<ResBookDetail>> getAllHasFilter(@PathVariable(name = "id") int id) {
        return new ResponseEntity<>(bookService.getDetail(id), HttpStatus.OK);
    }

    //    @PostMapping(value = "/book", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<List<ResBook>> add(
//            @RequestPart(required = false, name = "images") final MultipartFile[] images,
//            @RequestPart(required = false, name = "books") final ReqBook[] reqBookss
//    ) {
//        List<ReqBook> reqBooks = new ArrayList<>();
//        return new ResponseEntity<>(iBookService.add(reqBooks), HttpStatus.OK);
//    }
    @PostMapping(value = "/book", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<AnnounceResponse<List<ResBook>>> add(
            @RequestPart(required = false, name = "images") final MultipartFile[] images,
            @RequestPart(required = false, name = "books") final String reqBooks
    ) {
        return new ResponseEntity<>(bookService.add(images, reqBooks), HttpStatus.CREATED);
    }

    @PutMapping(value = "/book", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<AnnounceResponse<List<ResBook>>> edit(
            @RequestPart(required = false, name = "images") final MultipartFile[] images,
            @RequestPart(required = false, name = "books") final String reqBooks
    ) {
        return new ResponseEntity<>(bookService.edit(images, reqBooks), HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/book")
    public ResponseEntity<AnnounceResponse<Boolean>> del(@RequestBody List<Integer> ids) {
        return new ResponseEntity<>(bookService.del(ids), HttpStatus.OK);
    }

    @PostMapping(value = "/buy")
    public ResponseEntity<ResBill> authBuy(@RequestBody ReqBill reqBooks) {
        return new ResponseEntity<>(billService.create(reqBooks), HttpStatus.CREATED);
    }

//    @PatchMapping(value = "/buy/{visitorId}")
//    public ResponseEntity<List<ResBook>> buy(@PathVariable(name = "visitorId") int visitorId, @RequestBody List<ReqBook> req) {
//        return new ResponseEntity<>(iBookService.buy(visitorId, req), HttpStatus.OK);
//    }
}
