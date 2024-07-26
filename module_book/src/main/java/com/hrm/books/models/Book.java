package com.hrm.books.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book extends EvenTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(columnDefinition = "varchar(250)")
    String image;
    @Column(columnDefinition = "varchar(100)")
    String title;
    @Column
    double price;
    @Column
    short amount;
    @Column(name = "year_pub")
    short yearPub;

    @ManyToMany
    @JoinTable(
            name = "author_write",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    Set<Author> authors;

    @ManyToMany
    @JoinTable(
            name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    Set<Category> categories;

    public Book(String image, String title, double price, short amount, short yearPub) {
        this.image = image;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.yearPub = yearPub;
    }

    public Book(String image, String title, double price, short amount, short yearPub, Set<Author> authors, Set<Category> categories) {
        this.image = image;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.yearPub = yearPub;
        this.authors = authors;
        this.categories = categories;
    }

}
