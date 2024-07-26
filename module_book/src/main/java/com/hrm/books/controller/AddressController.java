package com.hrm.books.controller;

import com.hrm.books.service.IAddressService;
import com.hrm.books.utilities.dto.address.ResAddress;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressController {
    final IAddressService addressService;

    @GetMapping(path = "/address")
    public ResponseEntity<ResAddress[]> getAllHasFilter(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "15") int size) {
        return new ResponseEntity<>(addressService.getAllHasFilter(page, size), HttpStatus.OK);
    }

//    @PostMapping(path = "/address/{visitorId}")
//    public ResponseEntity<ResAddress> add(@PathVariable(name = "visitorId", required = false) int id, @RequestBody ReqAddress reqAddress) {
//        return new ResponseEntity<>(addressService.add(id, reqAddress), HttpStatus.CREATED);
//    }
//
//    @PutMapping(path = "/address/{addressId}")
//    public ResponseEntity<ResAddress> edit(@PathVariable(name = "addressId") int id, @RequestBody ReqAddress reqAddress) {
//        return new ResponseEntity<>(addressService.edit(id, reqAddress), HttpStatus.OK);
//    }
//
//    @DeleteMapping(path = "/address")
//    public ResponseEntity<Boolean> del(@RequestParam(name = "ids") int[] ids) {
//        return new ResponseEntity<>(addressService.del(ids), HttpStatus.OK);
//    }

}
