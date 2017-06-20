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
@Table(name = "tag_book", schema = "neurolib")
@NoArgsConstructor
public class TagBookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "book_id")
    private Long bookId;
}
