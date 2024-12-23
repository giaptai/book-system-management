package com.hrm.books.utilities.dto.bill;

import com.hrm.books.models.Address;
import com.hrm.books.models.Bill;
import com.hrm.books.models.BillDetail;
import com.hrm.books.models.Visitor;
import com.hrm.books.utilities.enums.State;
import com.hrm.books.utilities.dto.visitor.VisitorMap;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillMap {

    public static Bill MapToBill(int totalAmount, double totalPrice, State state, Visitor visitor, List<BillDetail> billDetails, Address address) {
        return new Bill(
                totalAmount,
                totalPrice,
                state,
                address.getNameReceive(),
                address.getPhoneReceive(),
                address.getLocation(),
                visitor,
                billDetails
        );
    }

    public static ResBill MapToResBill(Bill bill) {
        return new ResBill(
                bill.getId(),
                bill.getTotalAmount(),
                bill.getTotalPrice(),
                bill.getState().name(),
                bill.getBuyerName(),
                bill.getBuyerPhoneNumber(),
                bill.getDeliveryAddress(),
                VisitorMap.MapToResVisitorBuy(bill.getVisitorId()),
                bill.getCreate_at().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
        );
    }

    public static ResBillItems MapToResBillItems(BillDetail billDetail) {
        return new ResBillItems(
                billDetail.getId(),
                billDetail.getTitle(),
                billDetail.getPrice(),
                billDetail.getAmount(),
                billDetail.getTotalCost()
        );
    }

    public static ResBillDetail MapToResBillDetail(Bill bill) {
        return new ResBillDetail(
                MapToResBill(bill),
                bill.getBillDetails().stream().map(BillMap::MapToResBillItems).toList()
        );
    }
}
