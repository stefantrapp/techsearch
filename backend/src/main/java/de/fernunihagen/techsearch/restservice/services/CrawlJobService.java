package de.fernunihagen.techsearch.restservice.services;

import java.util.List;

import org.springframework.data.domain.Page;

import de.fernunihagen.techsearch.data.CrawlJob;

public interface CrawlJobService {

    List<CrawlJob> findCrawlJob(int page, int size, String sortDir, String sort);
    
    Page<CrawlJob> findCrawlJobByName(String name, int page, int size, String sortDir, String sort);
    
    void deleteCrawlJob(CrawlJob crawlJob);

    CrawlJob createCrawlJob(CrawlJob crawlJob);
    
    void updateCrawlJob(CrawlJob crawlJob);
    
}
