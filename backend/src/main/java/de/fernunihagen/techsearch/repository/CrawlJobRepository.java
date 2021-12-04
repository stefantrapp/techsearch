package de.fernunihagen.techsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.fernunihagen.techsearch.data.CrawlJob;

public interface CrawlJobRepository extends PagingAndSortingRepository<CrawlJob, Integer>, CrawlJobRepositoryCustom {
 
    @Query(value = "select * from crawl_job j where finished = false order by id LIMIT 1", nativeQuery = true)
    CrawlJob getNextUnfinishedCrawlJob();  
        
    Page<CrawlJob> findByNameLike(String name, Pageable pageable);
    
}
