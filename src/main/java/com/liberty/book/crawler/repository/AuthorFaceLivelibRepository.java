package com.liberty.book.crawler.repository;

import com.liberty.book.crawler.entity.AuthorFaceLivelibEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by user on 27.08.2017.
 */
public interface AuthorFaceLivelibRepository extends JpaRepository<AuthorFaceLivelibEntity, Long> {

    List<AuthorFaceLivelibEntity> findAllByAuthorAltNotAndNeurolibAuthorIdIsNull(String authorAlt);

    List<AuthorFaceLivelibEntity> findAllByNeurolibAuthorIdIsNull();

    @Transactional
    @Modifying
    void deleteAllByAuthorNameContaining(String name);

    @Query(nativeQuery = true, value = "SELECT * FROM neurolib.author_face_livelib t1 LEFT JOIN neurolib.libapics t2 ON t1.nl_author_id = t2.AvtorId WHERE t2.AvtorId IS NULL and t1.nl_author_id is not null;")
    List<AuthorFaceLivelibEntity> getEntityToDownloadFace();

}
