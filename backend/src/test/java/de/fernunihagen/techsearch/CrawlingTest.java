package de.fernunihagen.techsearch;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.data.CrawlJob;
import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.jobs.crawling.CrawlJobConfiguration;
import de.fernunihagen.techsearch.jobs.crawling.ProcessCrawlJobsJob;
import de.fernunihagen.techsearch.repository.CrawlJobRepository;
import de.fernunihagen.techsearch.restservice.services.AdministrationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlingTest {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private AdministrationService administrationService;
    
    @Autowired
    private CrawlJobRepository crawlJobRepository;
    
    
    @Test
    @Ignore
    public void testCrawling() {
        
        crawlJobRepository.findAll().forEach(j -> {
            crawlJobRepository.deleteCrawlJob(j);
        });
        
        var crawlJob = new CrawlJob();
        crawlJob.setId(1);
        crawlJob.setUrls(List.of("https://stefan-trapp-consulting.de/index.php/fokus"));
        
        crawlJob = crawlJobRepository.save(crawlJob);
        
        Document doc = new Document(); 
        
        doc.setName("Test");
        doc.setCrawlJob(crawlJob);
        
        ProcessCrawlJobsJob job = applicationContext.getBean(ProcessCrawlJobsJob.class);
        
        administrationService.resetProcessCrawlJobsJob();
        administrationService.scheduleProcessCrawlJobsJob();
        
        applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
        
        try {
            job.execute(null);
        } catch (JobExecutionException e) {
            fail();
        }
    }
    
    @Test
    @Ignore
    public void testImport() {
        ProcessCrawlJobsJob job = applicationContext.getBean(ProcessCrawlJobsJob.class);
        
        administrationService.resetProcessCrawlJobsJob();
        administrationService.scheduleProcessCrawlJobsJob();
        
        applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
        
        CrawlJobConfiguration crawlConfig = new CrawlJobConfiguration(); 
        crawlConfig.setUrl("http://localhost:8001/");
        
        try {
            job.execute(null);
        } catch (JobExecutionException e) {
            fail();
        }
    }
}
