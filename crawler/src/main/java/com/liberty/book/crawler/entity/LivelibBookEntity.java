package com.liberty.book.crawler.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by user on 16.06.2017.
 */
@Data
@AllArgsConstructor
@Entity
@Table(name = "livelib_books", schema = "neurolib")
@NoArgsConstructor
public class LivelibBookEntity {
    @Id
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "cover_url")
    private String cover;

    @Column(name = "author_names")
    private String authorNames;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "rating")
    private Float rating;
}
