package com.liberty.book.crawler.repository;

import com.liberty.book.crawler.entity.LinkEntity;
import com.liberty.book.crawler.entity.LivelibBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by user on 15.06.2017.
 */
@Repository
public interface LinksRepository extends JpaRepository<LinkEntity, Long> {
    LinkEntity getFirstByLinkEquals(String link);
}

