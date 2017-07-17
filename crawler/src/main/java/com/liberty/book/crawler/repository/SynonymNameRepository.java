package com.liberty.book.crawler.repository;

import com.liberty.book.crawler.entity.SynonymNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by user on 17.07.2017.
 */
public interface SynonymNameRepository  extends JpaRepository<SynonymNameEntity, Long> {
}
