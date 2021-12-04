package de.fernunihagen.techsearch.restservice.dtos;

import java.util.List;

public class EpoBulkImportDto {

    private List<String> files;

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
    
}
