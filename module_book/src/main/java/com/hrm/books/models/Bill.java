package com.hrm.books.models;

import com.hrm.books.utilities.enums.State;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bill extends EvenTime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "total_amount")
    int totalAmount;

    @Column(name = "total_price")
    double totalPrice;

    @Enumerated(EnumType.STRING)
    State state;

    @Column(name = "buyer_name", columnDefinition = "VARCHAR(50)")
    String buyerName;

    @Column(name="buyer_phone_number", columnDefinition = "VARCHAR(11)")
    String buyerPhoneNumber;

    @Column(name="delivery_address", columnDefinition = "VARCHAR(255)")
    String deliveryAddress;

    @JoinColumn(name = "visitor_id")
    @ManyToOne(targetEntity = Visitor.class)
    Visitor visitorId;

    @OneToMany(targetEntity = BillDetail.class, mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<BillDetail> billDetails;

    public Bill(int totalAmount, double totalPrice, State state, String buyerName,
                String buyerPhoneNumber, String deliveryAddress, Visitor visitorId,
                List<BillDetail> billDetails) {
        this.totalAmount = totalAmount;
        this.totalPrice = totalPrice;
        this.state = state;
        this.buyerName = buyerName;
        this.buyerPhoneNumber = buyerPhoneNumber;
        this.deliveryAddress = deliveryAddress;
        this.visitorId = visitorId;
        this.billDetails = billDetails;
    }
}
