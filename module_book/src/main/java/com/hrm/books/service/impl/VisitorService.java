package com.hrm.books.service.impl;

import com.hrm.books.repository.VisitorRepository;
import com.hrm.books.security.JWTService;
import com.hrm.books.security.VisitorDetailService;
import com.hrm.books.utilities.dto.visitor.PVisitor;
import com.hrm.books.utilities.enums.RMQQueueName;
import com.hrm.books.utilities.enums.Role;
import com.hrm.books.utilities.exceptions.MyException;
import com.hrm.books.utilities.dto.visitor.Login;
import com.hrm.books.utilities.dto.visitor.ReqVisitor;
import com.hrm.books.utilities.dto.visitor.ResLogin;
import com.hrm.books.utilities.dto.visitor.ResVisitor;
import com.hrm.books.models.Visitor;
import com.hrm.books.service.IVisitorService;
import com.hrm.books.utilities.dto.visitor.ResVisitorDetail;
import com.hrm.books.utilities.dto.visitor.VisitorMap;
// import com.hrm.books.utilities.rabbitmq.ProducerRabbit;
import com.hrm.books.utilities.regex.MyPattern;
import com.hrm.books.utilities.responses.AnnounceResponse;
import com.hrm.books.utilities.responses.DataResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VisitorService implements IVisitorService {
    final VisitorRepository visitorRepository;
    final VisitorDetailService visitorDetailService;
    final EntityManager em;
    final AuthenticationManager authenticationManager;
    final JWTService jwtService;
    final MyException myEx;
    // final ProducerRabbit producerRabbit;

    @Override
    public ResLogin login(Login login) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.username(), login.password()));
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = visitorDetailService.loadUserByUsername(login.username());
            return new ResLogin(
                    userDetails.getUsername(),
                    userDetails.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse(null),
                    jwtService.generateToken(userDetails)
            );
        } else throw myEx.new ErrorNotFound("Không tìm thấy");
    }

    @Override
    public ResVisitor register(ReqVisitor reqVisitor) {
        if (!MyPattern.compareEmail(reqVisitor.email())) {
            throw myEx.new myGlobalError("Email Invalid");
        }
        if (!MyPattern.comparePassword(reqVisitor.password())) {
            throw myEx.new myGlobalError("Password Invalid");
        }
        ResVisitor resVisitor = null;
        try {
            ReqVisitor[] reqVisitors = {reqVisitor};
            resVisitor = add(reqVisitors)[0];
            return resVisitor;
        } catch (RuntimeException e) {
            throw myEx.new myGlobalError(e.getMessage());
        } finally {
            if (resVisitor != null) {
                PVisitor PVisitor = new PVisitor(resVisitor.email(), resVisitor.username(), reqVisitor.password());
//                StringBuilder builder = new StringBuilder("Username: " + resVisitor.username());
//                builder.append("\n").append("Password: ").append(reqVisitor.password());
//                producerRabbit.sendSimpleMessage(visitorRabbit.toString());
                // producerRabbit.sendMessage(RMQQueueName.REGISTER.getValue(), PVisitor);
//                emailInit.sendSimpleMail(resVisitor.email(), "Register successfully !!!", builder.toString());
            }
        }
    }

    @Override
    public DataResponse<ResVisitor[]> getAllHasFilter(String email, String username, Role[] role, int page, int size) {
        return findWithFilter(email, username, role, page, size);
    }

    @Override
    public AnnounceResponse<ResVisitorDetail> getDetail(int id) {
        ResVisitorDetail resVisitorDetail = visitorRepository.findById(id).map(VisitorMap::MapToResVisitorDetail).orElseThrow(() -> myEx.new ErrorNotFound("Không tìm thấy"));
        return new AnnounceResponse<>(
                HttpStatus.OK.value(),
                resVisitorDetail
        );
    }

    @Override
    public ResVisitor[] add(ReqVisitor[] reqVisitors) {
        List<Visitor> visitors = new ArrayList<>();
        for (int i = 0; i < reqVisitors.length; i++) {
            String email = reqVisitors[i].email();
            if (checkEmailValid(email)) {
                String fullname = checkUsername(reqVisitors[i].name());
                Visitor visitor = VisitorMap.MapToVisitorCreate(reqVisitors[i]);
                visitor.setUsername(fullname);
                visitors.add(visitor);
            }
        }
        ResVisitor[] resVisitors = visitorRepository.saveAll(visitors).stream().map(VisitorMap::MapToResVisitor).toArray(value -> new ResVisitor[value]);
        return resVisitors;
    }

    @Override
    public ResVisitor[] edit(ReqVisitor[] reqVisitors) {
        return new ResVisitor[0];
    }

    @Override
    public ResVisitor edit(ReqVisitor reqVisitors) {
        return null;
    }

    @Override
    public ResVisitor edit(int id, ReqVisitor reqVisitors) {
        return null;
    }

    @Override
    public AnnounceResponse<Boolean> del(int id) {
        Visitor visitor = visitorRepository.findById(id).orElseThrow(() -> myEx.new ErrorNotFound("Không tìm thấy"));
        visitorRepository.delete(visitor);
        return new AnnounceResponse<>(
                HttpStatus.OK.value(),
                true
        );
    }

    private DataResponse<ResVisitor[]> findWithFilter(String email, String username, Role[] role, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Visitor> cq = cb.createQuery(Visitor.class);
        CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);

        Root<Visitor> visitorR = cq.from(Visitor.class);
        Root<Visitor> visitorRCount = cqCount.from(Visitor.class);

        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> predicatesCount = new ArrayList<>();

        if (email != null && !email.isEmpty()) {
            Predicate preEmail = cb.like(cb.lower(visitorR.get("email")), "%" + email.toLowerCase() + "%");
            Predicate preEmailCount = cb.like(cb.lower(visitorRCount.get("email")), "%" + email.toLowerCase() + "%");
            predicates.add(preEmail);
            predicatesCount.add(preEmailCount);
        }
        if (username != null && !username.isEmpty()) {
            Predicate preUsername = cb.like(cb.lower(visitorR.get("username")), "%" + username.toLowerCase() + "%");
            Predicate preUsernameCount = cb.like(cb.lower(visitorRCount.get("username")), "%" + username.toLowerCase() + "%");
            predicates.add(preUsername);
            predicatesCount.add(preUsernameCount);
        }
        if (role != null && role.length > 0) {
            Predicate preRole =  visitorR.get("role").in(Arrays.asList(role));
//            Predicate preRole = cb.in(visitorR.get("role")).value(role);
//            Predicate preRoleCount =  visitorR.get("role").in(Arrays.asList(role));
            Predicate preRoleCount = cb.in(visitorRCount.get("role")).value(Arrays.asList(role));
            predicates.add(preRole);
            predicatesCount.add(preRoleCount);
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cqCount.select(cb.count(visitorRCount));

        if (!predicates.isEmpty()) {
            cqCount.where(cb.and(predicatesCount.toArray(new Predicate[0])));
        }

        // Execute count query
        long totalSize = em.createQuery(cqCount).getSingleResult();
        int totalPage = (int) Math.ceil((double) totalSize / size);

        ResVisitor[] visitors = em.createQuery(cq)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultStream().map(VisitorMap::MapToResVisitor).toArray(ResVisitor[]::new);

        DataResponse<ResVisitor[]> response = new DataResponse<>(
                (short) HttpStatus.OK.value(),
                visitors,
                totalSize,
                page,
                totalPage
        );
        return response;
    }


    private String checkUsername(String name) {
        StringBuilder username = new StringBuilder();

        String[] fullName = name.split(" ");

        String normalized = Normalizer.normalize(fullName[fullName.length - 1], Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");
        username.append(withoutDiacritics);

        for (int j = fullName.length - 2; j >= 0; j--) {
            username.append(Character.toUpperCase(fullName[j].charAt(0)));
        }
        int visitorSameNameCount = visitorRepository.countByUsernameStartingWith(username.toString()) + 1;
        username.append(visitorSameNameCount);

        return username.toString();
    }

    private boolean checkEmailValid(String email) {
        Pattern pattern = Pattern.compile("^\\w+@[A-Za-z0-9.-]+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
