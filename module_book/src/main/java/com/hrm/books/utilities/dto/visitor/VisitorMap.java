package com.hrm.books.utilities.dto.visitor;

import com.hrm.books.models.Visitor;

public class VisitorMap {

    public static Visitor MapToVisitorCreate(ReqVisitor req) {
        StringBuilder builder = new StringBuilder("{noop}");
        return new Visitor(
                req.name(),
                req.email(),
                builder.append(req.password()).toString(),
                req.role()
        );
    }

    public static ResVisitor MapToResVisitor(Visitor visitor) {
        return new ResVisitor(
                visitor.getId(),
                visitor.getName(),
                visitor.getEmail(),
                visitor.getUsername(),
                visitor.getRole()
        );
    }

    public static ResVisitorDetail MapToResVisitorDetail(Visitor visitor) {
        return new ResVisitorDetail(
                visitor.getId(),
                visitor.getName(),
                visitor.getEmail(),
                visitor.getUsername(),
                visitor.getRole(),
                (short) visitor.getAddresses().size(),
                visitor.getBills().size()
        );
    }

    public static ResVisitorAuth MapToResVisitorBuy(Visitor visitor) {
        return new ResVisitorAuth(
                visitor.getEmail(),
                visitor.getUsername()
        );
    }
}
