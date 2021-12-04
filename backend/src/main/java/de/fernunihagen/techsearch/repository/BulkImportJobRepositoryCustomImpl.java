package de.fernunihagen.techsearch.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import de.fernunihagen.techsearch.data.BulkImportJob;

public class BulkImportJobRepositoryCustomImpl implements BulkImportJobRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    @Transactional
    public void deleteBulkImportJob(BulkImportJob bulkImportJob) {
        Query query = entityManager.createQuery("UPDATE Document d SET d.bulkImportJob = null WHERE d.bulkImportJob = :bulkImportJob");
        query.setParameter("bulkImportJob", bulkImportJob);
        query.executeUpdate();
        
        var job = entityManager.find(BulkImportJob.class, bulkImportJob.getId());
        entityManager.remove(job);
    }

    @Override
    @Transactional
    public void deleteAllBulkImportJobs() {
        Query query = entityManager.createQuery("UPDATE Document d SET d.bulkImportJob = null");
        query.executeUpdate();
        
        Query queryDeletAll = entityManager.createQuery("DELETE BulkImportJob");
        queryDeletAll.executeUpdate();
    }

}