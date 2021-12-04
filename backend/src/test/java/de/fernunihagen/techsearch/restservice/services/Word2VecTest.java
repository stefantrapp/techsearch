package de.fernunihagen.techsearch.restservice.services;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.restservice.dtos.SearchDto;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Word2VecTest {

    @Autowired
    private Word2VecService word2VecService;
    
    @Test
    @Ignore
    public void testWord2Vec() {
        var searchDto = new SearchDto(); 
        word2VecService.search(searchDto);
    }
    
}
