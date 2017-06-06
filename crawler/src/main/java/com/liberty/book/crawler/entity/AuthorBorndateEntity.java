package com.liberty.book.crawler.entity;

import com.liberty.book.crawler.common.DayBornObject;
import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by user on 06.06.2017.
 */
@Entity
@Table(name = "author_born_date", schema = "flibusta")
@Data
public class AuthorBorndateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "ll_author_id")
    private Long livelibAuthorId;
    @Column(name = "nl_author_id")
    private Long neurolibAuthorId;
    @Column(name = "born_date")
    private Long bornDate;
    @Column(name = "author_name")
    private String authorName;


}