package de.fernunihagen.techsearch.jobs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.services.AdministrationService;
import de.fernunihagen.techsearch.restservice.services.IndexQueryParameter;
import de.fernunihagen.techsearch.restservice.services.IndexSearchService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RebuildSearchIndexJobTest {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private AdministrationService administrationService;
    
    @Autowired
    private IndexSearchService indexSearchService;
    
	@Test
	@Ignore
	public void preprocess() {
	    RebuildSearchIndexJob job = new RebuildSearchIndexJob();

	    administrationService.resetRebuildSearchIndexJob();
        administrationService.scheduleRebuildSearchIndexJob();
	    
	    applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
	    
	    try {
            job.execute(null);
        } catch (Exception e) {
            fail(e);
        }
	    
        var searchResults = indexSearchService.query(new SearchDto("causes", Language.ENGLISH));
        assertNotNull(searchResults);
        assertTrue(searchResults.getEntries().size() > 0);
        
        var searchResultsLemma = indexSearchService.query(new SearchDto("cause", Language.ENGLISH), new IndexQueryParameter(true, true));
        assertNotNull(searchResultsLemma);
        assertTrue(searchResultsLemma.getEntries().size() > 0);
	}
}
