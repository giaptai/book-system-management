package com.hrm.books.controller;

import com.hrm.books.utilities.dto.author.ReqAuthor;
import com.hrm.books.utilities.dto.author.ResAuthor;
import com.hrm.books.service.IAuthorService;
import com.hrm.books.utilities.dto.author.ResAuthorDetail;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorController {
    final IAuthorService iAuthorService;

    @Operation(
            parameters = @Parameter(
                    in = ParameterIn.QUERY,
                    name = "nameOrder",
                    schema = @Schema(
                            allowableValues = {"ASC", "DESC"}
                    )
            )
    )
    @GetMapping(path = "/author")
    public ResponseEntity<DataResponse<ResAuthor[]>> getAllHasFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "ASC", name = "nameOrder") String nameOrder,
            @RequestParam(required = false) String bookName,
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "15", required = false) int size) {
        return new ResponseEntity<>(iAuthorService.getAllHasFilter(name, nameOrder, bookName, page, size), HttpStatus.OK);
    }

    @GetMapping(path = "/author/{id}")
    public ResponseEntity<AnnounceResponse<ResAuthorDetail>> getDetail(@PathVariable(name = "id") int id) {
        return new ResponseEntity<>(iAuthorService.getDetail(id), HttpStatus.OK);
    }

    @PostMapping(path = "/author")
    public ResponseEntity<AnnounceResponse<ResAuthor[]>> add(@RequestBody ReqAuthor[] reqAuthor) {
        return new ResponseEntity<>(iAuthorService.add(reqAuthor), HttpStatus.CREATED);
    }

    @PutMapping(path = "/author")
    public ResponseEntity<AnnounceResponse<ResAuthor[]>> edit(@RequestBody ReqAuthor[] reqAuthor) {
        return new ResponseEntity<>(iAuthorService.edit(reqAuthor), HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/author")
    public ResponseEntity<AnnounceResponse<Boolean>> del(@RequestBody int[] ids) {
        return new ResponseEntity<>(iAuthorService.del(ids), HttpStatus.OK);
    }
}
