package com.hrm.books.service.impl;

import com.hrm.books.models.Address;
import com.hrm.books.repository.AddressRepository;
import com.hrm.books.service.IAddressService;
import com.hrm.books.utilities.dto.address.AddressMap;
import com.hrm.books.utilities.dto.address.ResAddress;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressService implements IAddressService {
    final AddressRepository addressRepository;

    @Override
    public ResAddress[] getAllHasFilter(int page, int size) {
        PageRequest pageRequest = PageRequest.of((page - 1), size);
        Page<Address> addressPage = addressRepository.findAll(pageRequest);
        return addressPage.stream().map(AddressMap::MapToResAddress).toArray(ResAddress[]::new);
    }

//    @Override
//    public ResAddress add(int visitorId, ReqAddress reqAddress) {
//        Visitor dataVisitor = currentUserContext.getContext();
//        int sizeAddress = addressRepository.countByVisitorId(visitorId);
//        if (sizeAddress == 5) {
//            throw myException.new ErrorMaxLimit("Addresses limited max 5");
//        }
//        Address address =  AddressMap.MapToAddress(reqAddress, dataVisitor);
//        addressRepository.save(address);
//        return AddressMap.MapToResAddress(address);
//    }

//    @Override
//    public ResAddress edit(int addressId, ReqAddress reqAddress) {
//        Address address = addressRepository.findById(addressId).orElseThrow(() -> myException.new ErrorNotFound("Không tìm thấy"));
//        address.setLocation(reqAddress.location());
//        addressRepository.save(address);
//        return AddressMap.MapToResAddress(address);
//    }

    @Override
    public boolean del(int[] ids) {
        Set<Integer> integers = Arrays.stream(ids).boxed().collect(Collectors.toSet());
        if (addressRepository.countByIdIn(integers) == integers.size()) {
            addressRepository.deleteAllByIdInBatch(integers);
            return true;
        }
        return false;
    }
}
