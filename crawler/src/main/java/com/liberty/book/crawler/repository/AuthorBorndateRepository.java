package com.liberty.book.crawler.repository;


import com.liberty.book.crawler.entity.AuthorBorndateEntity;
import com.liberty.book.crawler.entity.AuthorImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: Dimitr
 * Date: 24.04.2017
 * Time: 21:38
 */
@Repository
public interface AuthorBorndateRepository extends JpaRepository<AuthorBorndateEntity, Long> {

    List<AuthorBorndateEntity> findAllByNeurolibAuthorIdIsNull();

}
