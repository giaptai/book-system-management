package com.hrm.books.security;

import com.hrm.books.models.Visitor;
import com.hrm.books.repository.VisitorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VisitorDetailService implements UserDetailsService {
    final VisitorRepository visitorRepository;

    public VisitorDetailService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    /**
     * cần tìm hiểu lại role và authority
     * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Visitor> visitor = visitorRepository.findByUsername(username);
        if (visitor.isPresent()) {
            Visitor userGet = visitor.get();
            return User.builder()
                    .username(userGet.getUsername())
                    .password(userGet.getPassword())
//                    .authorities(userGet.getRole().name())
                    .roles(userGet.getRole().name())
                    .build();
        } else throw new UsernameNotFoundException("Not Found " + username + " OK !!!");
    }
}
