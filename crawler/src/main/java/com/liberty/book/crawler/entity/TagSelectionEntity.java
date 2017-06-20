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
@Table(name = "tag_selection", schema = "neurolib")
@NoArgsConstructor
public class TagSelectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "selection_id")
    private Long selectionId;
}
