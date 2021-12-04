package de.fernunihagen.techsearch.restservice.services;

import java.util.List;

import org.springframework.data.domain.Page;

import de.fernunihagen.techsearch.data.BulkImportJob;

public interface BulkImportJobService {

    List<BulkImportJob> findBulkImportJob(int page, int size, String sortDir, String sort);
    
    Page<BulkImportJob> findBulkImportJobByName(String name, int page, int size, String sortDir, String sort);
    
    void deleteBulkImportJob(BulkImportJob bulkImportJob);

    BulkImportJob createBulkImportJob(BulkImportJob bulkImportJob);
    
    void updateBulkImportJob(BulkImportJob bulkImportJob);
    
}
