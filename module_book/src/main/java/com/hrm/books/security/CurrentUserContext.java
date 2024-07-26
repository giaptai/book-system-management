package com.hrm.books.security;

import com.hrm.books.models.Visitor;
import com.hrm.books.repository.VisitorRepository;
import com.hrm.books.utilities.dto.visitor.ResVisitor;
import com.hrm.books.utilities.dto.visitor.VisitorMap;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserContext {
    private final VisitorRepository visitorRepository;

    public CurrentUserContext(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    public ResVisitor getResVisitor() {
        Authentication auth = getAuthentication();
        Optional<Visitor> visitor = visitorRepository.findByUsername(auth.getName());
        if (visitor.isPresent()) {
            Visitor userGet = visitor.get();
            return VisitorMap.MapToResVisitor(userGet);
        } else return null;
    }

    public Visitor getContext() {
        Authentication auth = getAuthentication();
        Optional<Visitor> visitor = visitorRepository.findByUsername(auth.getName());
        return visitor.orElse(null);
    }
}
