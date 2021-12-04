package de.fernunihagen.techsearch;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.services.Sent2VecService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Sent2VecTest {
	
	@Autowired
    private Sent2VecService sent2VecService;
    
    @Test
    @Ignore
    public void testSearch() {
    	/* Für den Test kann die Konfiguration überschrieben werden: 
    	sent2VecConfig.setSentencesPath("");
    	*/
    	var searchDto = new SearchDto();
    	searchDto.setSearchTerm("background art in general");
    	searchDto.setLanguage(Language.ENGLISH);
    	var results = sent2VecService.search(searchDto);
    	assertNotNull(results);
    	var result = results.get(0);
    	assertEquals(result.getLineNumber(), 1493);
    	//assertEquals(result.getSentence(), "in recent years, a low-density parity-check (ldpc) code has been widely used as the error correction code. ");
    	//assertEquals(result.getSimilarity(), 0.476473f);
    	
    	sent2VecService.quit();
    }
    
}
