package de.fernunihagen.techsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.fernunihagen.techsearch.data.BulkImportJob;

public interface BulkImportJobRepository extends PagingAndSortingRepository<BulkImportJob, Integer>, BulkImportJobRepositoryCustom {
 
    @Query(value = "select * from bulk_import_job j where finished = false order by id LIMIT 1", nativeQuery = true)
    BulkImportJob getNextUnfinishedBulkImportJob();  
        
    Page<BulkImportJob> findByNameLike(String name, Pageable pageable);
    
}
