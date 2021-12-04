package de.fernunihagen.techsearch.restservice.dtos;

public class DocumentQueryDto   {
    private String name;
    
    private String id;
    
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
