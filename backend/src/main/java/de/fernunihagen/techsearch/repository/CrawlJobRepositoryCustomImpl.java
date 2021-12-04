package de.fernunihagen.techsearch.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import de.fernunihagen.techsearch.data.CrawlJob;

public class CrawlJobRepositoryCustomImpl implements CrawlJobRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    @Transactional
    public void deleteCrawlJob(CrawlJob crawlJob) {
        Query query = entityManager.createQuery("UPDATE Document d SET d.crawlJob = null WHERE d.crawlJob = :crawljob");
        query.setParameter("crawljob", crawlJob);
        query.executeUpdate();
        
        var job = entityManager.find(CrawlJob.class, crawlJob.getId());
        entityManager.remove(job);
    }

}
