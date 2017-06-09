package com.liberty.book.crawler.repository;

import com.liberty.book.crawler.entity.QuoteAuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User: Dimitr
 * Date: 24.04.2017
 * Time: 21:38
 */
@Repository
public interface QuoteAuthorRepository extends JpaRepository<QuoteAuthorEntity, Integer> {

}
