package com.hrm.books.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(columnDefinition = "varchar(50)")
    String nameReceive;

    @Column(columnDefinition = "varchar(12)")
    String phoneReceive;

    @Column(columnDefinition = "TEXT")
    String location;

    @JoinColumn(name = "visitor_id")
    @ManyToOne(targetEntity = Visitor.class, fetch = FetchType.LAZY)
    Visitor visitor;

    public Address(String nameReceive, String phoneReceive, String location, Visitor visitor) {
        this.nameReceive = nameReceive;
        this.phoneReceive = phoneReceive;
        this.location = location;
        this.visitor = visitor;
    }
}
