package com.liberty.book.crawler.repository;

import com.liberty.book.crawler.entity.SelectionBooksEntity;
import com.liberty.book.crawler.entity.SelectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by user on 15.06.2017.
 */
@Repository
public interface SelectionBookRepository extends JpaRepository<SelectionBooksEntity, Long> {

    List<SelectionBooksEntity>getAllByLivelibBookId(Long livelibBookId);

}

