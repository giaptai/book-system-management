package com.hrm.books.service.impl;

import com.hrm.books.models.Address;
import com.hrm.books.models.Bill;
import com.hrm.books.models.BillDetail;
import com.hrm.books.models.Book;
import com.hrm.books.models.Visitor;
import com.hrm.books.repository.BillRepository;
import com.hrm.books.repository.BookRepository;
import com.hrm.books.repository.VisitorRepository;
import com.hrm.books.security.CurrentUserContext;
import com.hrm.books.service.IBillService;
import com.hrm.books.utilities.enums.RMQQueueName;
import com.hrm.books.utilities.enums.State;
import com.hrm.books.utilities.exceptions.MyException;
import com.hrm.books.utilities.dto.bill.BillMap;
import com.hrm.books.utilities.dto.bill.ReqBill;
import com.hrm.books.utilities.dto.bill.ResBill;
import com.hrm.books.utilities.dto.bill.ResBillDetail;
import com.hrm.books.utilities.dto.book.ReqBookBuy;
// import com.hrm.books.utilities.rabbitmq.ProducerRabbit;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
public class BillService implements IBillService {
    final private BookRepository bookRepository;
    final private BillRepository billRepository;
    final private VisitorRepository visitorRepository;
    final private EntityManager em;
    final private MyException myEx;
    final private CurrentUserContext currentUserContext;
    // final private ProducerRabbit producerRabbit;

    @Override
    public DataResponse<List<ResBill>> getAllHasFilter(String username, State state, int page, int size) {
        return getWithFilter(username, state, page, size);
    }

    @Override
    public AnnounceResponse<List<ResBill>> updateState(List<UUID> uuids, State state) {
        List<Bill> bills = billRepository.findAllById(uuids);
        bills.forEach(bill -> {
                    if (bill.getState() == State.PENDING) {
                        bill.setState(state);
                    } else throw myEx.new myGlobalError("Only can change PENDING bill STATUS");
                }
        );
        List<ResBill> resBills = bills.stream().map(BillMap::MapToResBill).toList();
        billRepository.saveAll(bills);
        // producerRabbit.sendMessage(RMQQueueName.PURCHASE_STATE.getValue(), resBills);
        return new AnnounceResponse<>(
                HttpStatus.ACCEPTED.value(),
                resBills
        );
    }

    @Override
    public ResBill create(ReqBill reqBill) {
//        Bill bill = null;
        try {
            Visitor visitor = Optional.ofNullable(currentUserContext.getContext()).orElseThrow(() -> myEx.new ErrorNotFound("NEED LOGIN TO BUY !!!"));
            Address addresses = visitor.getAddresses().stream().filter(address -> address.getId() == reqBill.addressId()).findFirst().orElseThrow(
                    () -> myEx.new ErrorNotFound("Don't match any address")
            );
            if (reqBill.books().size() > 11) {
                throw myEx.new myGlobalError("LIMIT BOOK CAN BUY IS 11 !!!");
            }
            List<ReqBookBuy> reqBooks = new ArrayList<>();
            Map<Integer, Short> bookMap = new HashMap<>();

            for (ReqBookBuy bookBuy : reqBill.books()) {
                bookMap.put(bookBuy.id(), (short) (bookMap.getOrDefault(bookBuy.id(), (short) 0) + bookBuy.amount()));
            }

            for (Map.Entry<Integer, Short> entry : bookMap.entrySet()) {
                reqBooks.add(new ReqBookBuy(entry.getKey(), entry.getValue()));
            }
//            Visitor visitor = visitorRepository.findById(visitorId).orElseThrow(() -> myEx.new ErrorNotFound("Không tìm thấy"));
            AtomicInteger totalAmount = new AtomicInteger(0);
            double[] totalPrice = {0.0};
            List<BillDetail> billDetails = new ArrayList<>();
            List<Book> books = bookRepository.findAllById(reqBooks.stream().map(ReqBookBuy::id).toList());

            // modifies the totalAmount variable inside the lambda expression,
            // which is not allowed directly.
            reqBooks.forEach(reqBook -> {
                for (Book book : books) {
                    if (reqBook.id() == book.getId()) {
                        if (reqBook.amount() > book.getAmount()) {
                            throw myEx.new ErrorMaxLimit("Number of buy is greater than number available in stock !");
                        }
                        short amount = (short) (book.getAmount() - reqBook.amount());
                        book.setAmount(amount);
                        //set BILL
                        totalAmount.addAndGet(reqBook.amount());
                        totalPrice[0] += (reqBook.amount() * book.getPrice());
                        //create BILL DETAIL
                        BillDetail billDetail = new BillDetail(
                                book.getTitle(),
                                book.getPrice(),
                                reqBook.amount(),
                                reqBook.amount() * book.getPrice()
                        );
                        billDetails.add(billDetail);
                        break;
                    }
                }
            });
//            Bill bill = new Bill(totalAmount.get(), totalPrice[0], State.PENDING, visitor, billDetails);
            Bill bill = BillMap.MapToBill(totalAmount.get(), totalPrice[0], State.PENDING, visitor, billDetails, addresses);
            billDetails.forEach(billDetail -> billDetail.setBill(bill));
            bookRepository.saveAll(books);
            billRepository.save(bill);
            ResBillDetail resBillDetail = BillMap.MapToResBillDetail(bill);
            // producerRabbit.sendMessage(RMQQueueName.PURCHASE.getValue(), resBillDetail);
            return BillMap.MapToResBill(bill);
        } catch (RuntimeException r) {
            System.err.println(r.getMessage());
            throw r;
        } finally {
            System.out.printf("%1$-15s", "PURCHASE SUCCESSFUL !!!!");
        }
    }

    @Override
    public boolean del(List<UUID> uuids) {
        List<Bill> bills = billRepository.findAllById(uuids).stream().filter(bill ->
                bill.getState().equals(State.PENDING)
        ).toList();
        if (uuids.size() == bills.size()) {
            billRepository.deleteAll(bills);
            return true;
        }
        throw myEx.new myGlobalError("You can only delete Bill with pending status");
    }

    @Override
    public AnnounceResponse<ResBillDetail> getDetails(UUID id) {
        Bill bill = billRepository.findById(id).orElseThrow(() -> myEx.new ErrorNotFound("NOT FOUND BILL'S ID !!!"));
        return new AnnounceResponse<>(
                HttpStatus.OK.value(),
                BillMap.MapToResBillDetail(bill)
        );
    }

    /**
     * Chức năng: CriteriaBuilder được sử dụng để tạo các thành phần của truy vấn như điều kiện (Predicate), biểu thức (Expression), sắp xếp (Order), và các hàm tổng hợp.
     * <br/>
     * Chức năng: CriteriaQuery được sử dụng để xác định cấu trúc tổng thể của truy vấn, như từ đâu (FROM clause), điều kiện nào (WHERE clause), và sắp xếp như thế nào (ORDER BY clause).
     */
    private DataResponse<List<ResBill>> getWithFilter(String username, State state, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Bill> cq = cb.createQuery(Bill.class);
        CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);

        Root<Bill> billR = cq.from(Bill.class);
        Root<Bill> billRCount = cqCount.from(Bill.class);

        Join<Bill, Visitor> billJ = billR.join("visitorId", JoinType.LEFT);
        Join<Bill, Visitor> billJCount = billRCount.join("visitorId", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> predicateCount = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            predicates.add(cb.like(cb.lower(billJ.get("username")), "%" + username.toLowerCase() + "%"));
            predicateCount.add(cb.like(cb.lower(billJCount.get("username")), "%" + username.toLowerCase() + "%"));
        }
        if (state != null) {
            predicates.add(cb.equal(billR.get("state"), state));
            predicateCount.add(cb.equal(billRCount.get("state"), state));
        }

        cq.orderBy(cb.desc(billR.get("create_at")));
        cqCount.select(cb.count(billRCount));

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
            cqCount.where(predicateCount.toArray(new Predicate[0]));
        }

        long totalRecords = em.createQuery(cqCount).getSingleResult();
        int totalPage = (int) Math.ceil((float) totalRecords / size);
        int position = (page - 1) * size;

        List<ResBill> bills = em.createQuery(cq)
                .setFirstResult(position)
                .setMaxResults(size)
                .getResultStream().map(BillMap::MapToResBill).toList();
        return new DataResponse<>(
                (short) HttpStatus.OK.value(),
                bills,
                totalRecords,
                page,
                totalPage
        );
    }
}
