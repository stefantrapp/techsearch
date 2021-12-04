package de.fernunihagen.techsearch.restservice.controller;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.fernunihagen.techsearch.data.BulkImportJob;
import de.fernunihagen.techsearch.restservice.ApiOkResponse;
import de.fernunihagen.techsearch.restservice.ApiResponse;
import de.fernunihagen.techsearch.restservice.dtos.BulkImportJobDto;
import de.fernunihagen.techsearch.restservice.dtos.BulkImportJobQueryDto;
import de.fernunihagen.techsearch.restservice.dtos.BulkImportJobQueryResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.PageableDto;
import de.fernunihagen.techsearch.restservice.services.BulkImportJobService;

@RestController
public class BulkImportJobController {
    
    @Autowired
    private BulkImportJobService bulkImportJobService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @PostMapping("/api/bulkimport/search")
    public ApiResponse<BulkImportJobQueryResponseDto> search(@RequestBody() BulkImportJobQueryDto bulkImportJobQueryDto) {
        var pageable = bulkImportJobQueryDto.getPageable();
        var page = bulkImportJobService.findBulkImportJobByName(bulkImportJobQueryDto.getName(), pageable.getPage(), pageable.getSize(), pageable.getSortDir(), pageable.getSort());
        
        var dtos = page.stream().map(cj -> modelMapper.map(cj, BulkImportJobDto.class)).collect(Collectors.toList());
        
        var result = new BulkImportJobQueryResponseDto();
        result.setPageable(modelMapper.map(page, PageableDto.class));
        result.setBulkImportJobs(dtos);
        
        return new ApiResponse<BulkImportJobQueryResponseDto>(result);
    }
    
    @PostMapping("/api/bulkimport/delete")
    public ApiResponse<String> delete(@RequestBody() BulkImportJobDto bulkImportJobDto) {
        
        var bulkImportJob = new BulkImportJob();
        bulkImportJob.setId(bulkImportJobDto.getId());
        
        bulkImportJobService.deleteBulkImportJob(bulkImportJob);
        
        return new ApiOkResponse();
    }
    
    @PostMapping("/api/bulkimport/create")
    public ApiResponse<String> create(@RequestBody() BulkImportJobDto bulkImportJobDto) {
        var crawlJob = modelMapper.map(bulkImportJobDto, BulkImportJob.class);
        
        bulkImportJobService.createBulkImportJob(crawlJob);
        
        return new ApiOkResponse();
    }
    
    @PostMapping("/api/bulkimport/update")
    public ApiResponse<String> update(@RequestBody() BulkImportJobDto bulkImportJobDto) {
        var crawlJob = modelMapper.map(bulkImportJobDto, BulkImportJob.class);
        
        bulkImportJobService.updateBulkImportJob(crawlJob);
        
        return new ApiOkResponse();
    }
}
