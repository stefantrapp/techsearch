package de.fernunihagen.techsearch.repository;

import de.fernunihagen.techsearch.data.BulkImportJob;

public interface BulkImportJobRepositoryCustom {

    public void deleteBulkImportJob(BulkImportJob bulkImportJob);
    
    public void deleteAllBulkImportJobs();
    
}
