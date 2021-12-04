package de.fernunihagen.techsearch.jobs;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import de.fernunihagen.techsearch.jobs.crawling.ProcessCrawlJobsJob;

/**
 * Konfiguration für den Quartz-Scheduler.
 */
@Configuration
@EnableAutoConfiguration
public class QuartzScheduler {

    /* Nur für Unit-Tests! */
    public static boolean DisableTriggers;
    
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {    
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean scheduler(Set<Trigger> triggers, Map<String, JobDetail> jobs, DataSource quartzDataSource) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));
        schedulerFactory.setJobFactory(springBeanJobFactory());
        
        if (!DisableTriggers) {
            schedulerFactory.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
        }
        
        return schedulerFactory;
    }

    @Bean(name = "createSent2VecModelJobBean")
    public JobDetailFactoryBean createSent2VecModelJobJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(CreateSent2VecModelJob.class);
        jobDetailFactory.setName("CreateSent2VecModelJob");
        jobDetailFactory.setDescription("Sent2Vec Model erstellen");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }
    
    @Bean
    public SimpleTriggerFactoryBean createSent2VecModelJobTrigger(@Qualifier("createSent2VecModelJobBean") JobDetail job) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setRepeatInterval(10000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Trigger" + job);
        return trigger;
    }

    @Bean(name = "preprocessDocumentJobBean")
    public JobDetailFactoryBean preprocessDocumentJobJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(PreprocessDocumentJob.class);
        jobDetailFactory.setName("PreprocessDocumentJob");
        jobDetailFactory.setDescription("Dokumente vorverarbeiten");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }
    
    @Bean
    public SimpleTriggerFactoryBean preprocessDocumentJobTrigger(@Qualifier("preprocessDocumentJobBean") JobDetail job) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setRepeatInterval(10000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Trigger" + job);
        return trigger;
    }
    
    
    @Bean(name = "rebuildSearchIndexJobBean")
    public JobDetailFactoryBean rebuildSearchIndexJobJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(RebuildSearchIndexJob.class);
        jobDetailFactory.setName("RebuildSearchIndexJob");
        jobDetailFactory.setDescription("Suchindex aufbauen");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }
    
    @Bean
    public SimpleTriggerFactoryBean rebuildSearchIndexJobTrigger(@Qualifier("rebuildSearchIndexJobBean") JobDetail job) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setRepeatInterval(10000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Trigger" + job);
        return trigger;
    }
    
    
    @Bean(name = "bulkImportBatchJobBean")
    public JobDetailFactoryBean bulkImportBatchJobBeanJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(BulkImportBatchJob.class);
        jobDetailFactory.setName("BulkImportBatchJob");
        jobDetailFactory.setDescription("Bulkdaten importieren");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }
    
    @Bean
    public SimpleTriggerFactoryBean bulkImportBatchJobBeanTrigger(@Qualifier("bulkImportBatchJobBean") JobDetail job) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setRepeatInterval(10000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Trigger" + job);
        return trigger;
    }
    
    @Bean(name = "processCrawlJobsJobBean")
    public JobDetailFactoryBean processCrawlJobsJobBeanJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(ProcessCrawlJobsJob.class);
        jobDetailFactory.setName("ProcessCrawlJobsJob");
        jobDetailFactory.setDescription("Webcrawl-Jobs ausführen");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }
    
    @Bean
    public SimpleTriggerFactoryBean processCrawlJobsJobBeanBeanTrigger(@Qualifier("processCrawlJobsJobBean") JobDetail job) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setRepeatInterval(10000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Trigger" + job);
        return trigger;
    }
}