package com.hrm.books.utilities.dto.address;

import com.hrm.books.models.Address;
import com.hrm.books.models.Visitor;

public class AddressMap {
    public static Address MapToAddress(ReqAddress req, Visitor visitor) {
        return new Address(
                req.nameReceive(),
                req.phoneReceive(),
                req.location(),
                visitor
        );
    }

    public static ResAddress MapToResAddress(Address req) {
        return new ResAddress(
                req.getId(),
                req.getNameReceive(),
                req.getPhoneReceive(),
                req.getLocation(),
//                VisitorMap.MapToResVisitor(req.getVisitor())
                req.getVisitor().getUsername()
        );
    }

    public static ResAddressA MapToResAddressA(Address req) {
        return new ResAddressA(
                req.getId(),
                req.getNameReceive(),
                req.getPhoneReceive(),
                req.getLocation()
        );
    }
}
