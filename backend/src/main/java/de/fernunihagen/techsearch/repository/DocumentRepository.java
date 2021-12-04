package de.fernunihagen.techsearch.repository;

import java.util.stream.Stream;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.data.Language;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Query("select d from Document d where LENGTH(d.sentences) > 1 order by id")
    Stream<Document> findDocumentsWithSentences();
    

    @Query(value = "select * from Document d where d.sentences is not null and language = :language",  nativeQuery = true)
    @QueryHints(value = { @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE, value = "50")})
    Stream<Document> findDocumentsWithSentences(@Param("language") int language);
    
    @Query("select count (*) from Document d where LENGTH(d.sentences) > 1 and language = :language")
    long countDocumentsWithSentences(@Param("language") Language language);
    
    @Query("select d from Document d where LENGTH(d.sentences) > 1 order by id")
    Slice<Document> findDocumentWithSentences2(Pageable pageable);
    
    @Query(value = "select * from Document d where (d.sentences is null) and (d.language = 0 or d.language = 1 or d.language is null) order by id LIMIT 500", nativeQuery = true)
    Stream<Document> findNotPreprocessedDocumentsWithLimit();
    
    @Query(value = "select * from Document d where (d.sentences is null) and (d.language = 0 or d.language = 1 or d.language = 2 or d.language is null) order by id OFFSET :offset LIMIT :limit", nativeQuery = true)
    Stream<Document> findNotPreprocessedDocumentsFromTo(@Param("offset") long offset, @Param("limit") long limit);
    
    @Query(value = "select * from Document d order by id LIMIT 500", nativeQuery = true)
    Stream<Document> findDocumentsWithLimit();
    

    @Query(value = "select count(*) from Document d where (d.sentences is null) and (d.language = 0 or d.language = 1 or d.language = 2 or d.language is null)", nativeQuery = true)
    long countNotPreprocessedDocuments();
    
    @Query(value = "select count(*) from Document d", nativeQuery = true)
    long countDocuments();
    
    @Query(value = "select case when (count(*) > 0)  then true else false end from Document d where d.name = :name")
    boolean documentWithNameExists(@Param("name") String name);
    
    Page<Document> findByNameLike(String name, Pageable pageable);
    
}   