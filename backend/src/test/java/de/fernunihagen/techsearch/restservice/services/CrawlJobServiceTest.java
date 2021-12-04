package de.fernunihagen.techsearch.restservice.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.data.CrawlJob;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlJobServiceTest {

    @Autowired
    private CrawlJobService crawlJobService;
    
    @Test
    @Ignore
    public void testfindCrawlJobByNames() {
        
        int crawlJobsCountToCreate = 105;
        
        for (int i = 0; i < crawlJobsCountToCreate; i++) {
            var crawlJob = new CrawlJob();
            
            crawlJob.setName("TestJob " + i);
            crawlJob.setDescription("Test");
            
            crawlJobService.createCrawlJob(crawlJob);
        } 
        
        var allCrawlJobs = new ArrayList<CrawlJob>();
        
        for (int page = 0; page < crawlJobsCountToCreate; page++) {
            var jobs = crawlJobService.findCrawlJobByName("TestJob %", page, 10, "DESC", "id");
            assertNotNull(jobs);
            
            if (jobs.getContent().size() == 0) {
                break;
            }
            
            allCrawlJobs.addAll(jobs.getContent());
        }
        
        assertTrue(allCrawlJobs.size() >= crawlJobsCountToCreate);
        
        for (var crawlJob : allCrawlJobs) {
            System.out.println(crawlJob.getId() + ": " + crawlJob.getName());
            crawlJobService.deleteCrawlJob(crawlJob);
        }
        
        var jobsAfterDelete = crawlJobService.findCrawlJobByName("TestJob%", 0, 10, "DESC", "id");
        assertNotNull(jobsAfterDelete);
        assertTrue(jobsAfterDelete.getContent().size() == 0);
    }
    
    
    
}
