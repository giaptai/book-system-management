package com.hrm.books.controller;

import com.hrm.books.service.ICategoryService;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;
import com.hrm.books.utilities.dto.category.ReqCategory;
import com.hrm.books.utilities.dto.category.ResCategory;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryController {
    final ICategoryService iCategoryService;
    // https://stackoverflow.com/questions/24398653/how-to-get-swagger-uis-parameter-to-be-dropdown-menu-instead-of-text-input
    @Operation(
            parameters = @Parameter(
                    in = ParameterIn.QUERY,
                    name = "nameOrder",
                    schema = @Schema(allowableValues = {"ASC", "DESC"})

            )
    )
    @GetMapping(path = "/category")
    public ResponseEntity<DataResponse<ResCategory[]>> getAllHasFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "ASC", name = "nameOrder") String nameOrder,
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "15", required = false) int size) {
        return new ResponseEntity<>(iCategoryService.getAllHasFilter(name, nameOrder, page, size), HttpStatus.OK);
    }

    @PostMapping(path = "/category")
    public ResponseEntity<AnnounceResponse<ResCategory[]>> add(@RequestBody ReqCategory[] reqCategories) {
        return new ResponseEntity<>(iCategoryService.add(reqCategories), HttpStatus.CREATED);
    }

    @PutMapping(path = "/category")
    public ResponseEntity<AnnounceResponse<ResCategory[]>> edit(@RequestBody ReqCategory[] reqCategories) {
        return new ResponseEntity<>(iCategoryService.edit(reqCategories), HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/category")
    public ResponseEntity<AnnounceResponse<Boolean>> del(@RequestBody int[] ids) {
        return new ResponseEntity<>(iCategoryService.del(ids), HttpStatus.OK);
    }
}
