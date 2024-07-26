package com.hrm.books.models;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bill_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillDetail extends EvenTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(columnDefinition = "varchar(100)")
    String title;

    @Column
    double price;

    @Column
    short amount;

    @Column(name = "total_cost")
    double totalCost;

    @ManyToOne(targetEntity = Bill.class)
    @JoinColumn(name = "bill_id")
    Bill bill;

    public BillDetail(String title, double price, short amount, double totalCost) {
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.totalCost = totalCost;
    }
}
