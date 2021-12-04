package de.fernunihagen.techsearch.restservice.dtos;

import java.util.List;

public class CrawlJobQueryResponseDto {

    private List<CrawlJobDto> crawlJobs;

    private PageableDto pageable; 
    
    public List<CrawlJobDto> getCrawlJobs() {
        return crawlJobs;
    }

    public void setCrawlJobs(List<CrawlJobDto> crawlJobs) {
        this.crawlJobs = crawlJobs;
    }

    public PageableDto getPageable() {
        return pageable;
    }

    public void setPageable(PageableDto pageable) {
        this.pageable = pageable;
    }
    
}
