package de.fernunihagen.techsearch;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.services.PosTagSearchService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PosTagSearchServiceTest {

    @Autowired
    private PosTagSearchService posTagSearchService;
    
    @Test
    @Ignore
    public void testSearch() {
        var searchDto = new SearchDto();
        searchDto.setLanguage(Language.GERMAN);
        
        //shrinkage cause
        
        searchDto.setSearchTerm("trittabschnitt");
        posTagSearchService.search(searchDto);
         
        
    }
    
}
