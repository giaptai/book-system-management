package com.hrm.books.models;

import com.hrm.books.utilities.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
//https://www.baeldung.com/jpa-unique-constraints
@Table(name = "visitor", uniqueConstraints = {
        @UniqueConstraint(name = "ue_email", columnNames = {"email"}),
        @UniqueConstraint(name = "ue_username", columnNames = {"username"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Visitor extends EvenTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(columnDefinition = "varchar(150)")
    String name;
    @Column(columnDefinition = "varchar(50)")
    String email;
    @Column(columnDefinition = "varchar(20)")
    String username;
    @Column(columnDefinition = "varchar(26)")
    String password;

    @OneToMany(targetEntity = Address.class, mappedBy = "visitor", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    Set<Address> addresses;

    @OneToMany(mappedBy = "visitorId")
    Set<Bill> bills;

    @Column
    @Enumerated(EnumType.STRING)
    Role role;

    public Visitor(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Visitor(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
