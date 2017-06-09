package com.liberty.book.crawler.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Map;

/**
 * @author dkovalskyi
 * @since 01.06.2017
 */
@Entity
@Table(name = "quote", schema = "neurolib")
@Data
@NoArgsConstructor
public class QuoteEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "quote_author_id")
    private Integer quoteAuthorId;

    @Column(name = "flibusta_author_id")
    private Integer flibustaAuthorId;

    @Column(name = "quote_author_name")
    private String quoteAuthorName;

    @Column(name = "tags")
    @Convert(converter = MapConverter.class)
    private Map<Integer, String> tags;

}
