package com.liberty.book.crawler.repository;

import com.liberty.book.crawler.entity.TagBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by user on 15.06.2017.
 */
@Repository
public interface TagBookRepository extends JpaRepository<TagBookEntity, Long> {


}
