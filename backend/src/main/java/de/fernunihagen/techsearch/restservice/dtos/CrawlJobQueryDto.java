package de.fernunihagen.techsearch.restservice.dtos;

public class CrawlJobQueryDto   {
    private String name;
    
    private PageableDto pageable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PageableDto getPageable() {
        return pageable;
    }

    public void setPageable(PageableDto pageable) {
        this.pageable = pageable;
    }
    
}
