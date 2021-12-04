package de.fernunihagen.techsearch.restservice.controller;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.restservice.dtos.CrawlJobQueryDto;
import de.fernunihagen.techsearch.restservice.dtos.PageableDto;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlJobControllerTest {

    @Autowired
    private CrawlJobController crawlJobController;
    
    @Test
    @Ignore
    public void testSearch() {
        var crawlJobQueryDto = new CrawlJobQueryDto();
        crawlJobQueryDto.setName("TestJob%");
        
        var pageableDto = new PageableDto();
        pageableDto.setPage(0);
        pageableDto.setSize(10);
        pageableDto.setSortDir("DESC");
        pageableDto.setSort("id");
        crawlJobQueryDto.setPageable(pageableDto);
        
        var response = crawlJobController.search(crawlJobQueryDto);
        
        assertNotNull(response);
    }
}
