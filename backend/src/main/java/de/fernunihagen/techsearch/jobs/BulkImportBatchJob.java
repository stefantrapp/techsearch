package de.fernunihagen.techsearch.jobs;

import java.util.Date;

import javax.transaction.Transactional;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fernunihagen.techsearch.data.ApplicationJobEnum;
import de.fernunihagen.techsearch.data.BulkImportJobType;
import de.fernunihagen.techsearch.repository.ApplicationJobStateRepository;
import de.fernunihagen.techsearch.repository.BulkImportJobRepository;
import de.fernunihagen.techsearch.restservice.dtos.EpoBulkImportDto;
import de.fernunihagen.techsearch.restservice.services.EpoImportCallback;
import de.fernunihagen.techsearch.restservice.services.EpoImportService;

/**
 * Quarz-Job für den Massenimport.
 */
public class BulkImportBatchJob implements Job {

    private static Logger log = LoggerFactory.getLogger(BulkImportBatchJob.class);
    
    @Autowired
    private ApplicationJobStateRepository applicationJobStateRepository;
    
    @Autowired
    private BulkImportJobRepository bulkImportJobRepository;
    
    @Autowired
    private EpoImportService epoImportService;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    private String jobName;
    
    public BulkImportBatchJob() {
        jobName = getClass().getSimpleName();
    }
    
    private long lastPublicationCount = 0;
    
    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
    
        var jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.BULK_IMPORT_BATCH);
        if (jobState.isShouldRun() && !jobState.isRunning()) {
            try {
                log.info("Führe Job aus: '" + jobName + "'");
                
                jobState.setRunning(true);
                jobState.setStartTime(new Date());
                applicationJobStateRepository.save(jobState);
                
                var nextJob = bulkImportJobRepository.getNextUnfinishedBulkImportJob();
                log.info("Führe Bulk-Import aus: " + nextJob.getName());
                
                if (nextJob.getBulkImportJobType() == BulkImportJobType.EPO) {
                
                    var jsonData = nextJob.getBulkImportData();
                    
                    ObjectMapper objectMapper = new ObjectMapper();
                    var epoBulkImportDto = objectMapper.readValue(jsonData, EpoBulkImportDto.class);
                    
                    nextJob.setStartTime(new Date());
                    bulkImportJobRepository.save(nextJob);
                    
                    for (var file : epoBulkImportDto.getFiles()) {
                        epoImportService.importEpoFile(file, nextJob, new EpoImportCallback() {
                            
                            @Override
                            public void ImportProgess(long publicationCount) {
                                
                                if (publicationCount - 50 > lastPublicationCount) {
                                    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                                    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                                        @Override
                                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                                            var jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.BULK_IMPORT_BATCH);
                                            jobState.setLastSignOfLife(new Date());
                                            applicationJobStateRepository.save(jobState);
                                        }
                                    });
                                    
                                    
                                    lastPublicationCount = publicationCount;
                                }
                            }
                        });    
                    }
                    
                    nextJob.setFinished(true);
                    nextJob.setFinishedTime(new Date());
                    bulkImportJobRepository.save(nextJob);
                }
                
                log.info("Job beendet: '" + jobName + "'");
            } catch (Exception ex) {
                log.error("Fehler beim BulkImportBatch", ex);
            } finally {
                jobState.setRunning(false);
                jobState.setShouldRun(false);
                applicationJobStateRepository.save(jobState);
            }
        }
    }
}
