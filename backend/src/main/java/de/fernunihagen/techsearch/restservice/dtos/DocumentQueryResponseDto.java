package de.fernunihagen.techsearch.restservice.dtos;

import java.util.List;

public class DocumentQueryResponseDto {

    private List<DocumentBasicDto> documents;

    private PageableDto pageable; 
    
    public PageableDto getPageable() {
        return pageable;
    }

    public void setPageable(PageableDto pageable) {
        this.pageable = pageable;
    }

    public List<DocumentBasicDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentBasicDto> documents) {
        this.documents = documents;
    }
    
}
