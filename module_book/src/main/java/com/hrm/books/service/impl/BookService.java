package com.hrm.books.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.books.models.Author;
import com.hrm.books.models.Category;
import com.hrm.books.repository.BookRepository;
import com.hrm.books.utilities.exceptions.MyException;
import com.hrm.books.utilities.dto.book.BookMap;
import com.hrm.books.utilities.dto.book.ReqBook;
import com.hrm.books.models.Book;
import com.hrm.books.service.IBookService;
import com.hrm.books.utilities.dto.book.ResBook;
import com.hrm.books.utilities.dto.book.ResBookDetail;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hrm.books.utilities.constants.Constants.PATH_IMAGES;

@Service
@RequiredArgsConstructor
public class BookService implements IBookService {
    final private BookRepository bookRepository;
    final private EntityManager em;
    final private MyException myEx;
//    final private ProducerRabbit producerRabbit;

    @Override
    public DataResponse<List<ResBook>> getAllHasFilter(String title, String titleOrder, double priceFrom, double priceTo, short yearPub, String available, String authorName, String categoryNames, int page, int size) {
        return getWithFilter(title, titleOrder, priceFrom, priceTo, yearPub, available, authorName, categoryNames, page, size);
    }

    @Override
    public AnnounceResponse<ResBookDetail> getDetail(int id) {
//        producerRabbit.receiveSimpleMessage();
        Book book = bookRepository.findById(id).orElseThrow(() -> myEx.new ErrorNotFound("Not found Book By ID !!!"));
        return new AnnounceResponse<>(HttpStatus.OK.value(), BookMap.MapToResBookDetail(book));
    }

    @Override
    public List<ResBook> add(List<ReqBook> reqBooks) {
        return new ArrayList<>();
    }

    @Override
    public AnnounceResponse<List<ResBook>> add(MultipartFile[] file, String reqBooks) {
        try {
            List<Book> books = new ArrayList<>();
            List<ReqBook> reqBookList;
//            List<ReqBook> reqBookArray = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
//                reqBookArray = Arrays.asList(objectMapper.readValue(reqBooks, ReqBook[].class));
                reqBookList = objectMapper.readValue(reqBooks, new TypeReference<>() {
                });
                //dfdf
                MultipartFile currentFile;
                for (int i = 0; i < reqBookList.size(); i++) {
                    currentFile = (i < file.length) ? file[i] : null;
//                    System.err.printf(
//                            "%1$s\n%2$s\n%3$s\n%4$s\n",
//                            Optional.of(currentFile).orElse(""),
//                            Optional.ofNullable(currentFile.getContentType()).orElse(""),
//                            Optional.ofNullable(currentFile.getOriginalFilename()).orElse(""),
//                            Optional.ofNullable(currentFile.getInputStream()).orElse(null)
//                    );
                    if (currentFile != null && file[i].getSize() < 5_000_000) {
                        File fileRoot = new File(PATH_IMAGES);
                        if (fileRoot.mkdirs()) {
                            System.out.println("Folder is created successfully");
                        } else System.out.println("Folder is existed");
                        Path destination = Paths.get(PATH_IMAGES, currentFile.getOriginalFilename());
                        //copy file: file cần copy, đường dẫn mới
                        Files.copy(currentFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                    Book book = BookMap.MapToBookFile(currentFile, reqBookList.get(i));
                    books.add(book);
                }
                return new AnnounceResponse<>(
                        HttpStatus.CREATED.value(),
                        bookRepository.saveAll(books).stream().map(BookMap::MapToResBook).toList()
                );
            } catch (IOException e) {
                throw myEx.new myGlobalError("Stored image is issues " + e.getMessage());
            }
        } catch (RuntimeException r) {
            System.err.println(r.getMessage());
            throw myEx.new ErrorNotFound(r.getMessage());
        }
    }

//    @Override
//    public List<ResBook> edit(List<ReqBook> reqBooks) {
//        try {
//            List<Book> books = new ArrayList<>();
//            reqBooks.forEach(
//                    reqBook -> {
//                        Book book = BookMap.MapToBook(reqBook);
//                        books.add(book);
//                    }
//            );
//            return bookRepository.saveAll(books).stream().map(BookMap::MapToResBook).toList();
//        } catch (RuntimeException r) {
//            System.err.println(r.getMessage());
//            throw r;
//        }
//    }

    @Override
    public AnnounceResponse<List<ResBook>> edit(MultipartFile[] file, String reqBooks) {
        AnnounceResponse<List<ResBook>> response = add(file, reqBooks);
        response.setStatusCode(HttpStatus.ACCEPTED.value());
        return response;
    }

    @Override
    public AnnounceResponse<Boolean> del(List<Integer> ids) {
        List<Book> books = bookRepository.findAllById(ids);
        if (books.size() == ids.size()) {
            bookRepository.deleteAllByIdInBatch(ids);
            return new AnnounceResponse<>(HttpStatus.OK.value(), true);
        }
        throw myEx.new ErrorNotFound("NOT FOUND");
    }

//    @Override
//    public List<ResBook> buy(int visitorId, List<ReqBook> reqBooks) {
//        try {
//            Visitor visitor = visitorRepository.findById(visitorId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//            AtomicInteger totalAmount = new AtomicInteger(0);
//            double[] totalPrice = {0.0};
//            Set<BillDetail> billDetails = new HashSet<>();
//            List<BillDetail> billDetails = new ArrayList<>();
//            List<Book> books = bookRepository.findAllById(reqBooks.stream().map(ReqBook::id).toList());
    //The line totalAmount = totalAmount + book.getAmount();
    // modifies the totalAmount variable inside the lambda expression,
    // which is not allowed directly.
//            reqBooks.forEach(reqBook -> {
//                for (Book book : books) {
//                    if (reqBook.id() == book.getId()) {
//                        if (reqBook.amount() <= book.getAmount()) {
//                            //type promotion
//                            short amount = (short) (book.getAmount() - reqBook.amount());
//                            book.setAmount(amount);
//                        } else throw new ResponseStatusException(
//                                HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
//                                "Number of buy is greater than number available in stock !");
//                        break;
//                        if (reqBook.amount() > book.getAmount()) {
//                            throw new ResponseStatusException(
//                                    HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
//                                    "Number of buy is greater than number available in stock !");
//                        }
//                        short amount = (short) (book.getAmount() - reqBook.amount());
//                        book.setAmount(amount);
    //set BILL
//                        totalAmount.addAndGet(reqBook.amount());
//                        totalPrice[0] += (reqBook.amount() * book.getPrice());
    //create BILL DETAIL
//                        BillDetail billDetail = new BillDetail(
//                                book.getTitle(),
//                                book.getPrice(),
//                                reqBook.amount(),
//                                reqBook.amount() * book.getPrice()
//                        );
//                        billDetails.add(billDetail);
//                        break;
//                    }
//                }
//            });
//            Bill bill = new Bill(totalAmount.get(), totalPrice[0], State.PENDING, visitor, billDetails);
//            billDetails.forEach(billDetail -> billDetail.setBill(bill));
//            billRepository.save(bill);
//            return bookRepository.saveAll(books).stream().map(BookMap::MapToResBook).toList();
//        } catch (RuntimeException r) {
//            System.err.println(r.getMessage());
//            throw r;
//        }
//    }


    private DataResponse<List<ResBook>> getWithFilter(String title, String titleOrder, double priceFrom, double priceTo, short yearPub, String available, String authorName, String categoryNames, int page, int size) {
        //initial criteria builder
        CriteriaBuilder cb = em.getCriteriaBuilder();

        //initial builder query
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);

        //initial root
        Root<Book> bookR = cq.from(Book.class);
        Root<Book> bookRCount = cqCount.from(Book.class);

        // Initialize Predicates
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> predicateCounts = new ArrayList<>();

        //initial join
        Join<Book, Author> bookA = bookR.join("authors", JoinType.LEFT);
        Join<Book, Category> bookC = bookR.join("categories", JoinType.LEFT);

        Join<Book, Author> bookACount = bookRCount.join("authors", JoinType.LEFT);
        Join<Book, Category> bookCCount = bookRCount.join("categories", JoinType.LEFT);

        if (title != null && !title.isEmpty()) {
            String titleFilter = "%" + title.toLowerCase() + "%";
            Predicate predicateTitle = cb.like(cb.lower(bookR.get("title")), titleFilter);
            Predicate predicateTitleCount = cb.like(cb.lower(bookRCount.get("title")), titleFilter);
            predicates.add(predicateTitle);
            predicateCounts.add(predicateTitleCount);
        }
        if (authorName != null && !authorName.isEmpty()) {
            String authorNameFilter = "%" + authorName.toLowerCase() + "%";
            Predicate predicateAuthorName = cb.like(cb.lower(bookA.get("name")), authorNameFilter);
            Predicate predicateAuthorNamecOUNT = cb.like(cb.lower(bookACount.get("name")), authorNameFilter);
            predicates.add(predicateAuthorName);
            predicateCounts.add(predicateAuthorNamecOUNT);
        }
        if (categoryNames != null && !categoryNames.isEmpty()) {
            List<String> categoryNamesFilter = Arrays.asList(categoryNames.split(","));
            Predicate predicateCategoryNames = bookC.get("name").in(categoryNamesFilter);
            Predicate predicateCategoryNamescOUNT = bookCCount.get("name").in(categoryNamesFilter);
            predicates.add(predicateCategoryNames);
            predicateCounts.add(predicateCategoryNamescOUNT);
        }

        if (priceFrom > 0 && priceTo >= priceFrom) {
            Predicate predicatePrice = cb.between(bookR.get("price"), priceFrom, priceTo);
            Predicate predicatePriceCount = cb.between(bookRCount.get("price"), priceFrom, priceTo);
            predicates.add(predicatePrice);
            predicateCounts.add(predicatePriceCount);

        }
        if (yearPub > 0) {
            Predicate predicateYearRelease = cb.equal(bookR.get("yearPub"), yearPub);
            Predicate predicateYearReleaseCount = cb.equal(bookRCount.get("yearPub"), yearPub);
            predicates.add(predicateYearRelease);
            predicateCounts.add(predicateYearReleaseCount);
        }

        if (available != null && !available.isEmpty()) {
            Predicate predicateAvailable = available.equals("Y") ? cb.greaterThan(bookR.get("amount"), 0) : cb.equal(bookR.get("amount"), 0);
            Predicate predicateAvailableCount = available.equals("Y") ? cb.greaterThan(bookRCount.get("amount"), 0) : cb.equal(bookRCount.get("amount"), 0);
            predicates.add(predicateAvailable);
            predicateCounts.add(predicateAvailableCount);
        }

        cq.distinct(true).orderBy(titleOrder.equalsIgnoreCase("ASC") ? cb.asc(bookR.get("title")) : cb.desc(bookR.get("title"))).groupBy(bookR.get("id"));
//        cqCount.distinct(true).orderBy(titleOrder.equalsIgnoreCase("ASC") ? cb.asc(bookRCount.get("title")) : cb.desc(bookRCount.get("title")));

        cqCount.select(cb.countDistinct(bookRCount.get("id")));

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            cqCount.where(cb.and(predicateCounts.toArray(new Predicate[0])));
        }

        // Set distinct to avoid duplicate results
        // cq.distinct(true);
        // groupBy(bookR.get("id"))

        // Execute count query
        long totalSize = em.createQuery(cqCount).getSingleResult();
        int totalPage = (int) Math.ceil((double) totalSize / size);

        List<ResBook> books = em.createQuery(cq)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultStream().map(BookMap::MapToResBook).toList();
        DataResponse<List<ResBook>> responses = new DataResponse<>(
                (short) HttpStatus.OK.value(),
                books,
                totalSize,
                page,
                totalPage
        );
        return responses;
    }
}
