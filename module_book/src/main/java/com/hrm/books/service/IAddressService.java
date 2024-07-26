package com.hrm.books.service;

import com.hrm.books.utilities.dto.address.ReqAddress;
import com.hrm.books.utilities.dto.address.ResAddress;

public interface IAddressService {
    ResAddress[] getAllHasFilter(int page, int size);

    default ResAddress add(int visitorId, ReqAddress reqAddress) {
        return null;
    }

    ;

    default ResAddress edit(int addressId, ReqAddress reqAddress){return null;};

    boolean del(int[] ids);

    interface IBillService {

    }
}
