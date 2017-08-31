package com.liberty.book.crawler.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by user on 06.06.2017.
 */
@Entity
@Table(name = "author_face_livelib", schema = "neurolib")
@Data
public class AuthorFaceLivelibEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "ll_author_id")
    private Long livelibAuthorId;
    @Column(name = "nl_author_id")
    private Long neurolibAuthorId;
    @Column(name = "author_name")
    private String authorName;
    @Column(name = "author_alt")
    private String authorAlt;
    @Column(name = "face_url")
    private String faceUrl;

    @Override
    public String toString() {
        return "AuthorFaceLivelibEntity{" +
                "id=" + id +
                ", livelibAuthorId=" + livelibAuthorId +
                ", neurolibAuthorId=" + neurolibAuthorId +
                ", authorName='" + authorName + '\'' +
                ", authorAlt='" + authorAlt + '\'' +
                ", faceUrl='" + faceUrl + '\'' +
                '}';
    }
}
