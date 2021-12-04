package de.fernunihagen.techsearch.jobs;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.TestBase;
import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.services.AdministrationService;
import de.fernunihagen.techsearch.restservice.services.Sent2VecService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreateSent2VecModelJobTest extends TestBase {

	static {
		/* Dadurch wird die passende Konfiguration verwendet */
		System.setProperty("spring.profiles.active", "secondary");
	}
	
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private AdministrationService administrationService;
    
    @Autowired
    private Sent2VecService sent2VecService;
    
	@Test
	@Ignore
	public void preprocess() {
	    CreateSent2VecModelJob job = new CreateSent2VecModelJob();
	    
	    administrationService.resetSent2VecModelJob();
	    administrationService.scheduleRebuildSent2VecModelJob();
	    
	    applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
	    
	    try {
            job.execute(null);
        } catch (Exception e) {
            fail(e);
        }
	    
	    var toSearch = "the result is multiplied with a coefficient";
	    var searchDto = new SearchDto();
	    searchDto.setLanguage(Language.ENGLISH);
	    searchDto.setSearchTerm(toSearch);
	    var searchResults = sent2VecService.search(searchDto);
	    assertNotNull(searchResults);
	}
	
	

}
