package com.hrm.books.service.impl;

import com.hrm.books.models.Book;
import com.hrm.books.utilities.exceptions.MyException;
import com.hrm.books.utilities.dto.author.AuthorMap;
import com.hrm.books.utilities.dto.author.ReqAuthor;
import com.hrm.books.utilities.dto.author.ResAuthor;
import com.hrm.books.models.Author;
import com.hrm.books.repository.AuthorRepository;
import com.hrm.books.service.IAuthorService;
import com.hrm.books.utilities.dto.author.ResAuthorDetail;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorService implements IAuthorService {
    //dependence injection
    final AuthorRepository authorRepository;
    final EntityManager em;
    final MyException myE;

    @Override
    public DataResponse<ResAuthor[]> getAllHasFilter(String name, String nameOrder, String bookName, int page, int size) {
        return findWithFilter(name, nameOrder, bookName, page, size);
    }

    @Override
    public AnnounceResponse<ResAuthorDetail> getDetail(int id) {
        try {
            Author author = authorRepository.findById(id).orElseThrow(() -> myE.new ErrorNotFound("kHÔNG TÌM THẤY"));
            return new AnnounceResponse<>(
                    (short) HttpStatus.OK.value(),
                    AuthorMap.MapToResAuthorDetail(author)
            );
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public AnnounceResponse<ResAuthor[]> add(ReqAuthor[] reqAuthor) {
        try {
            List<Author> authors = Arrays.stream(reqAuthor).map(AuthorMap::MapToAuthor).toList();
            List<Author> savedAuthors = authorRepository.saveAll(authors);
            return new AnnounceResponse<>(
                    (short) HttpStatus.CREATED.value(),
                    savedAuthors.stream().map(AuthorMap::MapToResAuthor).toArray(value -> new ResAuthor[value])
            );
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public AnnounceResponse<ResAuthor[]> edit(ReqAuthor[] reqAuthor) {
        try {
            List<Author> authors = new ArrayList<>();
            for (int i = 0; i < reqAuthor.length; i++) {
                Author author = authorRepository.findById(reqAuthor[i].id()).orElseThrow(() -> myE.new ErrorNotFound("kHÔNG TÌM THẤY"));
                author.setName(reqAuthor[i].name());
                authors.add(author);
            }
            List<Author> savedAuthors = authorRepository.saveAll(authors);
            return new AnnounceResponse<>(
                    (short) HttpStatus.ACCEPTED.value(),
                    savedAuthors.stream().map(AuthorMap::MapToResAuthor).toArray(value -> new ResAuthor[value])
            );
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public AnnounceResponse<Boolean> del(int[] ids) {
        try {
            List<Integer> integers = Arrays.stream(ids).boxed().toList();
            List<Author> authors = authorRepository.findAllById(integers);
            if (authors.size() == integers.size()) {
                authorRepository.deleteAllByIdInBatch(integers);
                return new AnnounceResponse<>(
                        (short) HttpStatus.OK.value(),
                        true
                );
            }
            throw myE.new ErrorNotFound("kHÔNG TÌM THẤY");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    //    private ResAuthor[] findWithFilter(String name, String nameOrder, String bookName, int page, int size) {
//        //create builder
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//
//        CriteriaQuery<Author> cq = cb.createQuery(Author.class);
//        CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);
//
//        //Root
//        Root<Author> authorRoot = cq.from(Author.class);
//        Root<Author> authorRootCount = cqCount.from(Author.class);
//
//        // Initialize Predicates
//        List<Predicate> predicates = new ArrayList<>();
//
//        //Join
//        Join<Author, Book> bookJoin = authorRoot.join("books", JoinType.LEFT);
//        //Join<Author, Book> bookJoinCount = authorRootCount.join("books", JoinType.LEFT);
//
//        //Predicate
//        if (name != null && !name.isEmpty()) {
//            Predicate authorNamePredicate = cb.like(cb.lower(authorRoot.get("name")), "%" + name.toLowerCase() + "%");
//            predicates.add(authorNamePredicate);
//        }
//        if (bookName != null && !bookName.isEmpty()) {
//            Predicate bookNamePredicate = cb.like(cb.lower(bookJoin.get("title")), "%" + bookName.toLowerCase() + "%");
//            predicates.add(bookNamePredicate);
//        }
//        cq.orderBy(nameOrder.equals("DESC") ? cb.desc(authorRoot.get("name")) : cb.asc(authorRoot.get("name")));
//        cqCount.select(cb.count(authorRoot));
//
//        if (!predicates.isEmpty()) {
//            cq.where(cb.and(predicates.toArray(new Predicate[0])));
//            cqCount.where(cb.and(predicates.toArray(new Predicate[0])));
//        }
//
//        long totalCount = em.createQuery(cqCount).getSingleResult();
//        int totalPage = (int) Math.ceil((double) totalCount / size);
//        ResAuthor[] authors = em.createQuery(cq)
//                .setFirstResult((page - 1) * size)
//                .setMaxResults(size)
//                .getResultStream().map(AuthorMap::MapToResAuthor).toArray(value -> new ResAuthor[value]);
//        return authors;
//    }
    private DataResponse<ResAuthor[]> findWithFilter(String name, String nameOrder, String bookName, int page, int size) {
        // Create builder
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Main query
        CriteriaQuery<Author> cq = cb.createQuery(Author.class);
        Root<Author> authorRoot = cq.from(Author.class);
        List<Predicate> predicates = new ArrayList<>();

        // Join
        Join<Author, Book> bookJoin = authorRoot.join("books", JoinType.LEFT);

        // Predicates
        if (name != null && !name.isEmpty()) {
            Predicate authorNamePredicate = cb.like(cb.lower(authorRoot.get("name")), "%" + name.toLowerCase() + "%");
            predicates.add(authorNamePredicate);
        } else if (bookName != null && !bookName.isEmpty()) {
            Predicate bookNamePredicate = cb.like(cb.lower(bookJoin.get("title")), "%" + bookName.toLowerCase() + "%");
            predicates.add(bookNamePredicate);
        }
        cq.orderBy(nameOrder.equals("DESC") ? cb.desc(authorRoot.get("name")) : cb.asc(authorRoot.get("name")));

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // Count query
        CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);
        Root<Author> authorRootCount = cqCount.from(Author.class);
        List<Predicate> countPredicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            Predicate authorNameCountPredicate = cb.like(cb.lower(authorRootCount.get("name")), "%" + name.toLowerCase() + "%");
            countPredicates.add(authorNameCountPredicate);
        } else if (bookName != null && !bookName.isEmpty()) {
            // Join for count query
            Join<Author, Book> bookJoinCount = authorRootCount.join("books", JoinType.LEFT);
            Predicate bookNameCountPredicate = cb.like(cb.lower(bookJoinCount.get("title")), "%" + bookName.toLowerCase() + "%");
            countPredicates.add(bookNameCountPredicate);
        }
        cqCount.select(cb.count(authorRootCount));

        if (!countPredicates.isEmpty()) {
            cqCount.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }

        // Execute count query
        long totalCount = em.createQuery(cqCount).getSingleResult();
        int totalPage = (int) Math.ceil((double) totalCount / size);

        // Execute main query
        ResAuthor[] authors = em.createQuery(cq)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultStream().map(AuthorMap::MapToResAuthor).toArray(ResAuthor[]::new);
        DataResponse<ResAuthor[]> response = new DataResponse<>(
                (short) HttpStatus.OK.value(),
                authors,
                size,
                page,
                totalPage
        );
        return response;
    }

}
