package de.fernunihagen.techsearch.restservice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import de.fernunihagen.techsearch.data.BulkImportJob;
import de.fernunihagen.techsearch.data.BulkImportJobType;
import de.fernunihagen.techsearch.repository.BulkImportJobRepository;

@Service()
public class BulkImportJobServiceImpl implements BulkImportJobService  {

    @Autowired
    private BulkImportJobRepository bulkImportJobRepository;

    @Override
    public List<BulkImportJob> findBulkImportJob(int page, int size, String sortDir, String sort) {
        PageRequest pageReq = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sort);
        
        Page<BulkImportJob> bulkImportJobs = bulkImportJobRepository.findAll(pageReq);
        
        return bulkImportJobs.getContent();
    }
    
    @Override
    public Page<BulkImportJob> findBulkImportJobByName(String name, int page, int size, String sortDir, String sort) {
        PageRequest pageReq = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sort);
        
        Page<BulkImportJob> bulkImportJobs = bulkImportJobRepository.findByNameLike(name, pageReq);
        
        return bulkImportJobs;
    }

    @Override
    public void deleteBulkImportJob(BulkImportJob crawlJob) {
        bulkImportJobRepository.deleteBulkImportJob(crawlJob);
    }
    
    @Override
    public BulkImportJob createBulkImportJob(BulkImportJob bulkImportJob) {
        /* Es gibt aktuell nur einen Typ */
        bulkImportJob.setBulkImportJobType(BulkImportJobType.EPO);
        
        return bulkImportJobRepository.save(bulkImportJob);
    }
    
    @Override
    public void updateBulkImportJob(BulkImportJob bulkImportJob) {
        var oldJobOpt = bulkImportJobRepository.findById(bulkImportJob.getId());
        if (oldJobOpt.isPresent()) {
            var oldJob = oldJobOpt.get();
            oldJob.setDescription(bulkImportJob.getDescription());
            oldJob.setName(bulkImportJob.getName());
            oldJob.setBulkImportData(bulkImportJob.getBulkImportData());
            
            bulkImportJobRepository.save(oldJob);
        }
    }

}
