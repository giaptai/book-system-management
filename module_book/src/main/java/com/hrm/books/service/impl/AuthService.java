package com.hrm.books.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.books.models.Address;
import com.hrm.books.models.Bill;
import com.hrm.books.models.Visitor;
import com.hrm.books.repository.AddressRepository;
import com.hrm.books.repository.VisitorRepository;
import com.hrm.books.security.CurrentUserContext;
import com.hrm.books.utilities.dto.visitor.PVisitor;
import com.hrm.books.utilities.enums.RMQQueueName;
import com.hrm.books.utilities.exceptions.MyException;
import com.hrm.books.utilities.dto.address.AddressMap;
import com.hrm.books.utilities.dto.address.ReqAddress;
import com.hrm.books.utilities.dto.address.ResAddressA;
import com.hrm.books.utilities.dto.bill.BillMap;
import com.hrm.books.utilities.dto.bill.ResBill;
import com.hrm.books.utilities.dto.bill.ResBillDetail;
import com.hrm.books.utilities.dto.visitor.ReqVisitor;
import com.hrm.books.utilities.dto.visitor.ResVisitorDetail;
import com.hrm.books.utilities.dto.visitor.VisitorMap;
import com.hrm.books.utilities.rabbitmq.ProducerRabbit;
import com.hrm.books.utilities.regex.MyPattern;
import com.hrm.books.utilities.responses.AnnounceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final AddressRepository addressRepository;
    private final VisitorRepository visitorRepository;
    private final AddressService addressService;
    private final BillService billService;
    private final CurrentUserContext currentUserContext;
    private final MyException myE;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ProducerRabbit producerRabbit;

    public AuthService(AddressRepository addressRepository, VisitorRepository visitorRepository, AddressService addressService, BillService billService, CurrentUserContext currentUserContext, MyException myE, ProducerRabbit producerRabbit) {
        this.addressRepository = addressRepository;
        this.visitorRepository = visitorRepository;
        this.addressService = addressService;
        this.billService = billService;
        this.currentUserContext = currentUserContext;
        this.myE = myE;
        this.producerRabbit = producerRabbit;
    }

    /**
     * GET- ADD- EDIT
     */
    public AnnounceResponse<Object> authGeneral(String action, String id, Object req) {
        // https://www.geeksforgeeks.org/enhancements-for-switch-statement-in-java-13/
        return switch (action) {
            case "address" -> new AnnounceResponse<>(HttpStatus.OK.value(), authAddress(id, req));
            case "bill" -> new AnnounceResponse<>(HttpStatus.OK.value(), (id == null ? getBill() : getBillDetail(id)));
            case "password" -> new AnnounceResponse<>(HttpStatus.ACCEPTED.value(), changePassword(req));
            case "email" -> new AnnounceResponse<>(HttpStatus.ACCEPTED.value(), changeEmail(req));
            default -> new AnnounceResponse<>(HttpStatus.OK.value(), getInfo());
        };
    }

    public AnnounceResponse<Boolean> authDelete(String action, String[] ids) {
        Visitor visitor = currentUserContext.getContext();
        switch (action) {
            case "address":
                Set<Integer> idSet = Arrays.stream(ids).mapToInt(Integer::parseInt).boxed().collect(Collectors.toSet());
                int[] addresses = visitor.getAddresses().stream().filter(address ->
                        idSet.contains(address.getId())
                ).mapToInt(value -> value.getId()).toArray();
                if (addresses.length > 0) {
                    return new AnnounceResponse<>(HttpStatus.OK.value(), addressService.del(addresses));
                }
                throw myE.new ErrorNotFound("IDs not match with user id address");
            case "bill":
                Set<UUID> uuidSet = Arrays.stream(ids).map(UUID::fromString).collect(Collectors.toSet());
                List<UUID> uuidBill = visitor.getBills().stream().filter(bill ->
                        uuidSet.contains(bill.getId())).map(Bill::getId).toList();
                if (!uuidBill.isEmpty()) {
                    return new AnnounceResponse<>(HttpStatus.OK.value(), billService.del(uuidBill));
                }
                throw myE.new ErrorNotFound("IDs not match with user bill id");
            default:
                throw myE.new myGlobalError("Not found path variable !!!");
        }
    }

    private ResVisitorDetail getInfo() {
        Visitor visitor = currentUserContext.getContext();
        return visitor != null ? VisitorMap.MapToResVisitorDetail(visitor) : null;
    }

    private List<ResAddressA> authAddress(String id, Object req) {
        Visitor visitor = currentUserContext.getContext();
        if (visitor != null) {
            Set<Address> addresses = visitor.getAddresses();
            //khong the cast tu object xuong ReqAddress vi luc nay object o dang hashMap
            ReqAddress reqAddress = mapper.convertValue(req, ReqAddress.class);
            if (id != null && reqAddress != null) {
                int idInt = Integer.parseInt(id);
                addresses.stream().filter(address -> address.getId() == idInt).findFirst().ifPresentOrElse(
                        address -> {
                            address.setNameReceive(reqAddress.nameReceive());
                            address.setPhoneReceive(reqAddress.phoneReceive());
                            address.setLocation(reqAddress.location());
                        },
                        () -> {
                            int sizeAddress = addressRepository.countByVisitorId(visitor.getId());
                            if (sizeAddress == 5) {
                                throw myE.new ErrorMaxLimit("Addresses limited max is 5");
                            }
                            addresses.add(AddressMap.MapToAddress(reqAddress, visitor));
                        }
                );
                visitorRepository.save(visitor);
            }
            return addresses.stream().map(AddressMap::MapToResAddressA).toList();
        } else throw myE.new myGlobalError("USER isn't exist");
    }

    private List<ResBill> getBill() {
        Visitor visitor = currentUserContext.getContext();
        if (visitor != null) {
            Set<Bill> bills = visitor.getBills().stream().sorted((o1, o2) -> o2.getCreate_at().compareTo(o1.getCreate_at())).collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
//            Set<Bill> bills2 = visitor.getBills().stream().sorted((o1, o2) -> o2.getCreate_at().compareTo(o1.getCreate_at())).collect(Collectors.toSet());

            return bills.stream().map(BillMap::MapToResBill).toList();
        } else return new ArrayList<>();
    }

    private ResBillDetail getBillDetail(String id) throws IllegalArgumentException {
        try {
            UUID uuid = UUID.fromString(id);
            Visitor visitor = currentUserContext.getContext();
            if (visitor != null) {
                Set<Bill> bills = visitor.getBills();
                Bill bill = bills.stream().filter(item -> item.getId().equals(uuid)).findFirst().orElseThrow(() -> myE.new ErrorNotFound("Not found Bill"));
                return BillMap.MapToResBillDetail(bill);
            } else return null;
        } catch (IllegalArgumentException e) {
            throw myE.new myGlobalError("Illegal UUID !!!");
        }
    }

    private String changePassword(Object req) {
        Visitor visitor = currentUserContext.getContext();
        if (visitor != null) {
            ReqVisitor reqVisitor = mapper.convertValue(req, ReqVisitor.class);
            if (!MyPattern.comparePassword(reqVisitor.password())) {
                throw myE.new myGlobalError("Password Invalid");
            }
            visitor.setPassword("{noop}" + reqVisitor.password());
            visitorRepository.save(visitor);
            producerRabbit.sendMessage(RMQQueueName.CHANGE_PASSWORD.getValue(), new PVisitor(visitor.getEmail(), visitor.getUsername(), visitor.getPassword()));
//            emailInit.sendSimpleMail(reqVisitor.email(), "CHANGE PASSWORD SUCCESSFULLY !!!",
//                    "Hi " + visitor.getUsername() + ",\n\n" +
//                            "Your new password: " + reqVisitor.password() +
//                            "\n\nThanks,\nBook Empire"
//            );
            return "Change password successfully !!!";
        } else throw myE.new myGlobalError("USER isn't exist");
    }

    private String changeEmail(Object req) {
        Visitor visitor = currentUserContext.getContext();
        if (visitor != null) {
            ReqVisitor reqVisitor = mapper.convertValue(req, ReqVisitor.class);
            if (!MyPattern.compareEmail(reqVisitor.email())) {
                throw myE.new myGlobalError("Email Invalid");
            }
            visitor.setEmail(reqVisitor.email());
            visitorRepository.save(visitor);
            producerRabbit.sendMessage(RMQQueueName.CHANGE_EMAIL.getValue(), new PVisitor(visitor.getEmail(), visitor.getUsername(), visitor.getPassword()));
//            emailInit.sendSimpleMail(reqVisitor.email(), "CHANGE EMAIL SUCCESSFULLY !!!",
//                    "Hi " + visitor.getUsername() + ",\n\n" +
//                            "We will now be using this address for all future communication, including important updates, invoices, and project information.\n\n" +
//                            "Thanks,\nBook Empire"
//            );
            return "Change email successfully !!!";
        } else throw myE.new myGlobalError("USER isn't exist");
    }
}
