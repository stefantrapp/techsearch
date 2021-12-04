package de.fernunihagen.techsearch;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fernunihagen.techsearch.data.ApplicationJobName;
import de.fernunihagen.techsearch.data.BulkImportJob;
import de.fernunihagen.techsearch.data.BulkImportJobType;
import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.jobs.BulkImportBatchJob;
import de.fernunihagen.techsearch.repository.BulkImportJobRepository;
import de.fernunihagen.techsearch.repository.DocumentRepository;
import de.fernunihagen.techsearch.restservice.dtos.EpoBulkImportDto;
import de.fernunihagen.techsearch.restservice.dtos.JobStatusRequestDto;
import de.fernunihagen.techsearch.restservice.dtos.UpdateJobDto;
import de.fernunihagen.techsearch.restservice.services.AdministrationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BulkImportJobTest {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private BulkImportJobRepository bulkImportJobRepository;
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private AdministrationService administrationService;
    
    @Test
    @Ignore
    public void testBulkImport() {
        
        bulkImportJobRepository.deleteAllBulkImportJobs();
        
        var newJob = new BulkImportJob();
        
        newJob.setBulkImportJobType(BulkImportJobType.EPO);
        newJob.setBulkImportData("{ 'files': ['path/filea', [path/fileb]]}");
        newJob.setName("Test EPO Import");
                
        var savedBulkImportJob = bulkImportJobRepository.save(newJob);
        
        PageRequest pageReq = PageRequest.of(0, 10, Sort.Direction.fromString("ASC"), "name");
        
        var firstPage = bulkImportJobRepository.findByNameLike("Test%", pageReq);
        assertNotNull(firstPage);
        var content = firstPage.getContent();
        assertNotNull(content);
        var size = content.size();
        assertTrue(size == 1);
        
        var doc = new Document();
        doc.setName("Dokument von BulkImportJob");
        doc.setBulkImportJob(savedBulkImportJob);
        
        documentRepository.save(doc);
        
        bulkImportJobRepository.deleteBulkImportJob(savedBulkImportJob);
    }
    
    @Test
    @Ignore
    public void testBulkImportCreate() throws Exception {
        
        bulkImportJobRepository.deleteAllBulkImportJobs();
        
        var newJob = new BulkImportJob();
        
        newJob.setBulkImportJobType(BulkImportJobType.EPO);
        
        ObjectMapper objectMapper = new ObjectMapper();
        var epoBulkImportDto = new EpoBulkImportDto();
        epoBulkImportDto.setFiles(Arrays.asList(new String[] {"E:\\\\EPO\\\\Auszug.txt"}));
        var importData = objectMapper.writeValueAsString(epoBulkImportDto);
        
        newJob.setBulkImportData(importData);
        newJob.setName("Test EPO Import");
                
        bulkImportJobRepository.save(newJob);
        
        var updateDto = new UpdateJobDto();
        updateDto.setJobName(ApplicationJobName.BULK_IMPORT_BATCH);
        
        updateDto.setReset(true);
        administrationService.updateJob(updateDto);
        updateDto.setReset(false);
        
        updateDto.setSchedule(true);
        administrationService.updateJob(updateDto);
        
        var jobStatusRequestDto = new JobStatusRequestDto();
        jobStatusRequestDto.setJobName(ApplicationJobName.BULK_IMPORT_BATCH);
        
        var now = new Date();
        
        
        var bulkImportBatchJob = new BulkImportBatchJob();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(bulkImportBatchJob);
        
        try {
            bulkImportBatchJob.execute(null);
        } catch (JobExecutionException e) {
            fail();
        }
        
        while (true) {
            var status = administrationService.getJobStatus(jobStatusRequestDto);
            
            if (status.getStartTime().after(now) && !status.isRunning()) {
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
}
