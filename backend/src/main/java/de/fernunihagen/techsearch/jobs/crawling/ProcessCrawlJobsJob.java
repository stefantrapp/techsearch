package de.fernunihagen.techsearch.jobs.crawling;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fernunihagen.techsearch.data.ApplicationJobEnum;
import de.fernunihagen.techsearch.data.ApplicationJobState;
import de.fernunihagen.techsearch.data.CrawlJob;
import de.fernunihagen.techsearch.repository.ApplicationJobStateRepository;
import de.fernunihagen.techsearch.repository.CrawlJobRepository;
import de.fernunihagen.techsearch.restservice.services.DocumentService;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;


@Service
public class ProcessCrawlJobsJob implements Job {

    @Autowired
    private CrawlingConfig crawlingConfig;
    
    @Autowired
    private Logger log;
    
    @Autowired
    private DocumentService documentService; 
    
    @Autowired
    private CrawlJobRepository crawlJobRepository; 
    
    @Autowired
    private ApplicationJobStateRepository applicationJobStateRepository;
    
    
    private File crawlStorageFolder;
    
    public ProcessCrawlJobsJob() {
    }
    
    private String getStorageFolder(CrawlJob crawlJob) {
        try {
            var path = Path.of(crawlingConfig.getTempStoragePath(), Integer.toString(crawlJob.getId()));
            crawlStorageFolder = path.toFile().getCanonicalFile();
        
            log.info("Temporäres Verzeichnis für heruntergeladene Dateien: " + crawlStorageFolder.getAbsolutePath());
            
            if (!crawlStorageFolder.exists()) {
                log.info("Temporäres Verzeichnis für heruntergeladene Dateien existiert nicht. Es wird angelegt.");
                var created = crawlStorageFolder.mkdir();
                if (!created) {
                    log.warn("Das Verzeichnis konnte nicht angelegt werden. Bitte den Pfad und Berechtigungen prüfen.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return crawlStorageFolder.getAbsolutePath();
    }
    
	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.PROCESS_CRAWL_JOBS);
        if (jobState.isShouldRun() && !jobState.isRunning()) {
            try {
                jobState.setRunning(true);
                jobState.setStartTime(new Date());
                applicationJobStateRepository.save(jobState);
                
                log.info("Führe Job aus: 'ProcessCrawlJobsJob'");
                
                var crawlJob = crawlJobRepository.getNextUnfinishedCrawlJob();
                //var crawlJob = crawlJobRepository.findAll().get(0);
                
                String crawlStorageFolder = getStorageFolder(crawlJob);

                try {
                    crawl(jobState, crawlJob, crawlStorageFolder);
                    
                    var fileIterator = FileUtils.iterateFiles(new File(crawlStorageFolder), null, false);
                    
                    while (fileIterator.hasNext()) {
                        var file = fileIterator.next();
                        
                        documentService.importHtmlFile(file, crawlJob);
                        file.delete();
                    }
                    
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                
            } catch (Exception ex) {
                log.error("Fehler beim Crawlen", ex);
            } finally {
                jobState.setRunning(false);
                jobState.setShouldRun(false);
                applicationJobStateRepository.save(jobState);
            }
        }
    }

    protected void crawl(ApplicationJobState jobState, CrawlJob crawlJob, String crawlStorageFolder)
            throws Exception, InterruptedException {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setCleanupDelaySeconds(this.crawlingConfig.getCleanupDelaySeconds());
        config.setThreadShutdownDelaySeconds(this.crawlingConfig.getThreadShutdownDelaySeconds());
        config.setThreadMonitoringDelaySeconds(this.crawlingConfig.getThreadMonitoringDelaySeconds());
        config.setPolitenessDelay(this.crawlingConfig.getPolitenessDelay());
        config.setIncludeHttpsPages(true);
        config.setOnlineTldListUpdate(false);
        config.setResumableCrawling(true);
        int numberOfCrawlers = this.crawlingConfig.getNumberOfCrawlers();
        
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        
        var seeds = crawlJob.getUrls();
        
        for (var seed : seeds) {
            controller.addSeed(seed);    
        }
        
        CrawlController.WebCrawlerFactory<SiteCrawler> factory = new WebCrawlerFactory<SiteCrawler>() {

            @Override
            public SiteCrawler newInstance() throws Exception {
                return new SiteCrawler(crawlJob, crawlingConfig);
            }
        };
        
        crawlJob.setStartTime(new Date());
        crawlJobRepository.save(crawlJob);
        
        controller.startNonBlocking(factory, numberOfCrawlers);
        
//                    long start = System.currentTimeMillis();
        
        while (!controller.isFinished()) {
            Frontier frontier = controller.getFrontier();
            long scheduledPages = frontier.getNumberOfScheduledPages();
            long processedPages = frontier.getNumberOfProcessedPages();
            System.out.println("scheduledPages: " + scheduledPages + "; processedPages:" + processedPages);
            Thread.sleep(1000);
            
            jobState.setLastSignOfLife(new Date());
            applicationJobStateRepository.save(jobState);
            
//                        if (System.currentTimeMillis() - start > 10 * 1000) {
//                            log.warn("Beende Crawling wegen Zeitablauf.");
//                            controller.shutdown();
//                        }
        }
        
        controller.waitUntilFinish();
        
        crawlJob.setFinished(true);
        crawlJob.setFinishedTime(new Date());
        crawlJobRepository.save(crawlJob);
        
        
        log.info("Crawling abgeschlossen: " + crawlJob.getId() + ": " + String.join("\n", crawlJob.getUrls()));
    }
    

}
