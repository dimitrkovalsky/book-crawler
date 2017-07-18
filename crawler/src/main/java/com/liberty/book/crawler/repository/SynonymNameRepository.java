package com.liberty.book.crawler.repository;

import com.liberty.book.crawler.entity.SynonymNameEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by user on 17.07.2017.
 */
public interface SynonymNameRepository  extends JpaRepository<SynonymNameEntity, Long> {
   SynonymNameEntity findFirstByNameEquals(String name);

   List<SynonymNameEntity> findAllByNameId(Long nameId);
}
