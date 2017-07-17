package com.liberty.book.crawler.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by user on 15.06.2017.
 */
@Data
@AllArgsConstructor
@Entity
@Table(name = "synonym_names", schema = "neurolib")
@NoArgsConstructor
public class SynonymNameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name_id")
    private Long nameId;

    @Column(name = "name")
    private String name;
}
