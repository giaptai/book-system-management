package com.hrm.books.service.impl;

import com.hrm.books.models.Category;
import com.hrm.books.repository.CategoryRepository;
import com.hrm.books.service.ICategoryService;
import com.hrm.books.utilities.exceptions.MyException;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;
import com.hrm.books.utilities.dto.category.CategoryMap;
import com.hrm.books.utilities.dto.category.ReqCategory;
import com.hrm.books.utilities.dto.category.ResCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final EntityManager em;
    private final MyException myEx;

    @Override
    public DataResponse<ResCategory[]> getAllHasFilter(String name, String nameOrder, int page, int size) {
        return getWithFilter(name, nameOrder, page, size);
    }

    @Override
    public AnnounceResponse<ResCategory[]> add(ReqCategory[] reqCategories) {
        List<Category> list = Arrays.stream(reqCategories).map(CategoryMap::MapToCategory).toList();
        return new AnnounceResponse<>(
                HttpStatus.CREATED.value(),
                categoryRepository.saveAll(list).stream().map(CategoryMap::MapToResCategory).toArray(value -> new ResCategory[value])
        );
    }

    @Override
    public AnnounceResponse<ResCategory[]> edit(ReqCategory[] reqCategories) {
        AnnounceResponse<ResCategory[]> response = add(reqCategories);
        response.setStatusCode(HttpStatus.ACCEPTED.value());
        return response;
    }

    @Override
    public AnnounceResponse<Boolean> del(int[] ids) {
        List<Integer> integers = Arrays.stream(ids).boxed().toList();
        List<Category> categories = categoryRepository.findAllById(integers);
        if (integers.size() == categories.size()) {
            categoryRepository.deleteAllByIdInBatch(integers);
            return new AnnounceResponse<>(HttpStatus.OK.value(), true);
        }
        throw myEx.new ErrorNotFound("Không tìm thấy");
    }

    private DataResponse<ResCategory[]> getWithFilter(String name, String nameOrder, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);  // Query for getting the count of matching records

        Root<Category> categoryRoot = cq.from(Category.class);
        Root<Category> countRoot = cqCount.from(Category.class);

        // Initialize Predicates
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> predicateCount = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            Predicate categoryNamePredicate = cb.like(cb.lower(categoryRoot.get("name")), "%" + name.toLowerCase() + "%");
            Predicate categoryNamePredicateCount = cb.like(cb.lower(countRoot.get("name")), "%" + name.toLowerCase() + "%");
            predicates.add(categoryNamePredicate);
            predicateCount.add(categoryNamePredicateCount);
        }

        cq.orderBy(nameOrder.equals("ASC") ? cb.asc(categoryRoot.get("name")) : cb.desc(categoryRoot.get("name")));
        cqCount.select(cb.count(countRoot));

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            cqCount.where(cb.and(predicateCount.toArray(new Predicate[0])));
        }

        long totalRecords = em.createQuery(cqCount).getSingleResult();
        int totalPage = (int) Math.ceil((float) totalRecords / size);
        int position = (page - 1) * size;
        ResCategory[] categories = em.createQuery(cq)
                .setFirstResult(position)
                .setMaxResults(size)
                .getResultStream().map(CategoryMap::MapToResCategory).toArray(value -> new ResCategory[value]);
        DataResponse<ResCategory[]> response = new DataResponse<>(
                (short) HttpStatus.OK.value(),
                categories,
                totalRecords,
                page,
                totalPage
        );
        return response;
    }
}
