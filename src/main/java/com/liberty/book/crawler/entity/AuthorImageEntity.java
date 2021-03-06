package com.liberty.book.crawler.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * User: Dimitr
 * Date: 11.05.2017
 * Time: 21:29
 */
@Entity
@Table(name = "libapics", schema = "flibusta")
@Data
public class AuthorImageEntity {
    @Id
    @Column(name = "AvtorId")
    private Long authorId;
    @Column(name = "nid")
    private int nid;
    @Column(name = "File")
    private String file;
}
