package de.fernunihagen.techsearch;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.data.CrawlJob;
import de.fernunihagen.techsearch.restservice.dtos.CrawlJobDto;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MappingTest {

    @Autowired
    private ModelMapper modelMapper;
    
    @Test
    @Ignore
    public void testMapping() {
        var crawlJob = new CrawlJob();
        crawlJob.setId(42);
        crawlJob.setName("a name");
        crawlJob.setDescription("a description");
        crawlJob.setFinished(true);
        crawlJob.setUrls(List.of("url1", "url2"));
        
        CrawlJobDto orderDTO = modelMapper.map(crawlJob, CrawlJobDto.class);
        
        assertNotNull(orderDTO);
        assertEquals("url1\nurl2", orderDTO.getUrls());
        assertEquals(crawlJob.getId(), orderDTO.getId());
        assertEquals(crawlJob.getName(), orderDTO.getName());
        assertEquals(crawlJob.getDescription(), orderDTO.getDescription());
        assertEquals(crawlJob.isFinished(), orderDTO.isFinished());
    }

}
