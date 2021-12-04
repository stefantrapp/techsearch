package de.fernunihagen.techsearch.restservice.controller;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.fernunihagen.techsearch.data.CrawlJob;
import de.fernunihagen.techsearch.restservice.ApiOkResponse;
import de.fernunihagen.techsearch.restservice.ApiResponse;
import de.fernunihagen.techsearch.restservice.dtos.CrawlJobDto;
import de.fernunihagen.techsearch.restservice.dtos.CrawlJobQueryDto;
import de.fernunihagen.techsearch.restservice.dtos.CrawlJobQueryResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.PageableDto;
import de.fernunihagen.techsearch.restservice.services.CrawlJobService;

@RestController
public class CrawlJobController {

    @Autowired
    private CrawlJobService crawlJobService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @PostMapping("/api/crawljob/search")
    public ApiResponse<CrawlJobQueryResponseDto> search(@RequestBody() CrawlJobQueryDto crawlJobQueryDto) {
        var pageable = crawlJobQueryDto.getPageable();
        var page = crawlJobService.findCrawlJobByName(crawlJobQueryDto.getName(), pageable.getPage(), pageable.getSize(), pageable.getSortDir(), pageable.getSort());
        
        var dtos = page.stream().map(cj -> modelMapper.map(cj, CrawlJobDto.class)).collect(Collectors.toList());
        
        var result = new CrawlJobQueryResponseDto();
        result.setPageable(modelMapper.map(page, PageableDto.class));
        result.setCrawlJobs(dtos);
        
        return new ApiResponse<CrawlJobQueryResponseDto>(result);
    }
    
    @PostMapping("/api/crawljob/delete")
    public ApiResponse<String> delete(@RequestBody() CrawlJobDto crawlJobDto) {
        
        var crawlJob = new CrawlJob();
        crawlJob.setId(crawlJobDto.getId());
        
        crawlJobService.deleteCrawlJob(crawlJob);
        
        return new ApiOkResponse();
    }
    
    @PostMapping("/api/crawljob/create")
    public ApiResponse<String> create(@RequestBody() CrawlJobDto crawlJobDto) {
        var crawlJob = modelMapper.map(crawlJobDto, CrawlJob.class);
        
        crawlJobService.createCrawlJob(crawlJob);
        
        return new ApiOkResponse();
    }
    
    @PostMapping("/api/crawljob/update")
    public ApiResponse<String> update(@RequestBody() CrawlJobDto crawlJobDto) {
        var crawlJob = modelMapper.map(crawlJobDto, CrawlJob.class);
        
        crawlJobService.updateCrawlJob(crawlJob);
        
        return new ApiOkResponse();
    }
    
}
