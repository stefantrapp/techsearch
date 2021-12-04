package de.fernunihagen.techsearch.restservice.dtos;

import java.util.List;

public class Doc2VecSearchResponseDto {
    
    private List<String> documents;

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }
}
