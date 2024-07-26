package com.hrm.books.repository;

import com.hrm.books.models.Address;
import com.hrm.books.models.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    @Query
    int countByIdIn(Set<Integer> ids);

    @Query(value = "SELECT COUNT(a.id) from Address a WHERE a.visitor.id = :visitorId")
    int countByVisitorId(@Param(value = "visitorId") int visitorId);

    @Query
    List<Address> findAddressByVisitor(Visitor visitor);
}
