package com.liberty.book.crawler.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by user on 20.06.2017.
 */
@Data
@AllArgsConstructor
@Entity
@Table(name = "links", schema = "neurolib")
@NoArgsConstructor
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "link")
    private String link;
}
