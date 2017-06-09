package com.liberty.book.crawler.repository;

import com.liberty.book.crawler.entity.AuthorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: Dimitr
 * Date: 24.04.2017
 * Time: 21:38
 */
@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM libavtorname ORDER BY RAND() LIMIT :size")
    List<AuthorEntity> getRandomAuthors(@Param("size") int size);

    @Query(nativeQuery = true, value = "SELECT *  FROM libavtorname WHERE AvtorId IN (SELECT DISTINCT(AuthorId) AS a FROM neurolib.book_card WHERE GenreId = :genreId ORDER BY Rate DESC) AND AvtorId != :except LIMIT :size")
    List<AuthorEntity> getByGenre(@Param("genreId") Integer genreId, @Param("except") int except, @Param("size") int size);

    Page<AuthorEntity> getAllByFirstNameOrMiddleNameOrLastNameContainingOrderByLastName(Pageable pageable, String firstName, String middleName, String lastName);


    @Query(nativeQuery = true, value = "SELECT * FROM libavtorname WHERE LOWER(LastName) LIKE '%:name%'")
    List<AuthorEntity> getByLastName(@Param("name") String name);

    @Query(nativeQuery = true, value = "SELECT * FROM libavtorname WHERE LOWER(LastName) LIKE '%:lastName%' AND LOWER(FirstName) LIKE '%:firstName%'")
    List<AuthorEntity> getByLastAndFistName(@Param("lastName") String lastName, @Param("firstName") String firstName);
}
