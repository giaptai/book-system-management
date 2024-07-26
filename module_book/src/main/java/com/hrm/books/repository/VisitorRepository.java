package com.hrm.books.repository;

import com.hrm.books.models.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Integer> {
    int countByUsernameStartingWith(String username);

    Optional<Visitor> findByUsername(String username);
}
