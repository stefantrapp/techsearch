package de.fernunihagen.techsearch.restservice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import de.fernunihagen.techsearch.data.CrawlJob;
import de.fernunihagen.techsearch.repository.CrawlJobRepository;

@Service()
public class CrawlJobServiceImpl implements CrawlJobService  {

    @Autowired
    private CrawlJobRepository crawlJobRepository;

    @Override
    public List<CrawlJob> findCrawlJob(int page, int size, String sortDir, String sort) {
        PageRequest pageReq = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sort);
        
        Page<CrawlJob> crawlJobs = crawlJobRepository.findAll(pageReq);
        
        return crawlJobs.getContent();
    }
    
    @Override
    public Page<CrawlJob> findCrawlJobByName(String name, int page, int size, String sortDir, String sort) {
        PageRequest pageReq = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sort);
        
        Page<CrawlJob> crawlJobs = crawlJobRepository.findByNameLike(name, pageReq);
        
        return crawlJobs;
    }

    @Override
    public void deleteCrawlJob(CrawlJob crawlJob) {
        crawlJobRepository.deleteCrawlJob(crawlJob);
    }
    
    @Override
    public CrawlJob createCrawlJob(CrawlJob crawlJob) {
        return crawlJobRepository.save(crawlJob);
    }

    @Override
    public void updateCrawlJob(CrawlJob crawlJob) {
        var oldJobOpt = crawlJobRepository.findById(crawlJob.getId());
        if (oldJobOpt.isPresent()) {
            var oldJob = oldJobOpt.get();
            oldJob.setDescription(crawlJob.getDescription());
            oldJob.setName(crawlJob.getName());
            oldJob.setUrls(crawlJob.getUrls());
            
            crawlJobRepository.save(oldJob);
        }
    }

}
