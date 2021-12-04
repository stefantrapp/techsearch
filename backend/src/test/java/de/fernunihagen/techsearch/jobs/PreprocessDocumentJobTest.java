package de.fernunihagen.techsearch.jobs;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.TestBase;
import de.fernunihagen.techsearch.restservice.services.AdministrationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PreprocessDocumentJobTest extends TestBase {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private AdministrationService administrationService;
    
	@Test
	@Ignore
	public void preprocess() {
	    PreprocessDocumentJob job = new PreprocessDocumentJob();
	    
	    job.setUseOnlyOnePreporcessor(true);
	    PreprocessDocumentJob.setCreatePosModel(false);
	    Preprocessor.createPosModel = PreprocessDocumentJob.isCreatePosModel();
	    
	    administrationService.resetPreprocessDocumentsJob();
        administrationService.schedulePreprocessDocumentsJob();
	    
	    //job.setMaxDocumentsToProcess(1000);
	    
	    applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
	    
	    try {
            job.execute(null);
        } catch (Exception e) {
            fail(e);
        }
	}
}
