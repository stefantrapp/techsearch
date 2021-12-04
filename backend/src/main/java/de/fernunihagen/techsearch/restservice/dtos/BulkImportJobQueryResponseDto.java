package de.fernunihagen.techsearch.restservice.dtos;

import java.util.List;

public class BulkImportJobQueryResponseDto {

    private List<BulkImportJobDto> bulkImportJobs;

    private PageableDto pageable; 

    public PageableDto getPageable() {
        return pageable;
    }

    public void setPageable(PageableDto pageable) {
        this.pageable = pageable;
    }

    public List<BulkImportJobDto> getBulkImportJobs() {
        return bulkImportJobs;
    }

    public void setBulkImportJobs(List<BulkImportJobDto> bulkImportJobs) {
        this.bulkImportJobs = bulkImportJobs;
    }
    
}
