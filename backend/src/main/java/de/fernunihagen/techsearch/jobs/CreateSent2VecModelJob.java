package de.fernunihagen.techsearch.jobs;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import de.fernunihagen.techsearch.data.ApplicationJobEnum;
import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.repository.ApplicationJobStateRepository;
import de.fernunihagen.techsearch.repository.DocumentRepository;
import de.fernunihagen.techsearch.restservice.services.Sent2VecConfig;
import de.fernunihagen.techsearch.restservice.services.Sent2VecWrapper;
import de.fernunihagen.techsearch.restservice.services.Sent2VecWrapper.CreateModelStatus;

/**
 * Quarz-Job für die Erzeugung des Sent2BVec-Modells.
 */
public class CreateSent2VecModelJob implements Job {

    private static Logger log = LoggerFactory.getLogger(CreateSent2VecModelJob.class);
    
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
    private ApplicationJobStateRepository applicationJobStateRepository;

	@Autowired
	private AutowireCapableBeanFactory beanFactory;
	
	@Autowired
    EntityManagerFactory managerFactory;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	private Sent2VecConfig sent2VecConfig;
	
	public CreateSent2VecModelJob() {
	}
	
	@Override
	@Transactional
	public void execute(JobExecutionContext context) throws JobExecutionException {
    
        var jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.REBUILD_SENT2VEC_MODEL);
        if (jobState.isShouldRun() && !jobState.isRunning()) {
            try {
                log.info("Führe Job aus: 'CreateSent2VecModelJob'");
                
                jobState.setRunning(true);
                jobState.setStartTime(new Date());
                applicationJobStateRepository.save(jobState);
                
                var languages = List.of(Language.ENGLISH, Language.GERMAN);
                
                Map<Language, FileWriter> writers = new HashMap<Language, FileWriter>(2);
                
                languages.forEach(language -> {
                    FileWriter fileWriter = null;
                    if ((fileWriter = FileWriter.create(sent2VecConfig.getSentencesPath().replace("{@Lang}", language.toString()))) == null) {
                       return; 
                    }
                    writers.put(language, fileWriter);    
                });
                
                if (writers.size() < languages.size()) {
                    return;
                }
            
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        var documents = documentRepository.findDocumentsWithSentences();
                        
                        AtomicInteger documentWrittenCounter = new AtomicInteger(0);
                        
                        var documentIterator = documents.iterator();
                        while (documentIterator.hasNext()) {
                            var doc = documentIterator.next(); 
                            
                            int documentsWritten = documentWrittenCounter.getAndIncrement();
                            
                            var sentences = doc.getSentences();
                            var writer = writers.get(doc.getLanguage());
                            
                            if (writer != null) {
                            	var sentencesList = sentences.split("\n");
                            	
	                            for (var sentence : sentencesList) {
	                                
	                                sentence = sentence.toLowerCase(); /* Sent2Vec braucht die Sätze in Kleinbuchstaben */
	                                
	                                if (sentence.length() < 10) {
	                                    continue;
	                                }
	                                
	                                if (!writer.writeLine(sentence)) {
	                                    break;
	                                }
	                            }
                            }
                            
                            if (documentsWritten % 500 == 0) {
                                TransactionTemplate transactionTemplate2 = new TransactionTemplate(transactionManager);
                                transactionTemplate2.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                                transactionTemplate2.execute(new TransactionCallbackWithoutResult() {
                                    @Override
                                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                                        var jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.REBUILD_SENT2VEC_MODEL);
                                        jobState.setLastSignOfLife(new Date());
                                        applicationJobStateRepository.save(jobState);
                                    }
                                });    
                            }
                        }
                        
                        writers.values().forEach(fw -> fw.close());
                    }
                });
                
                log.info("Dateien mit Sätzen wurden geschrieben");
                

                for (var language : languages) {
	                Sent2VecWrapper sent2VecWrapper = new Sent2VecWrapper(sent2VecConfig, language);
	                beanFactory.autowireBean(sent2VecWrapper);
	                
	                sent2VecWrapper.startCreateModel();
	                
	                CreateModelStatus finished;
	                while (true) {
						finished = sent2VecWrapper.isCreateModelFinished();
	                	
	                	var status = sent2VecWrapper.getCreateModelStatus();
	                	
	                	var marker = "Progress: ";
	                	var indexProgress = status.lastIndexOf(marker);
	                	if (indexProgress == -1) {
	                		log.debug("Status der Modelerzeugung: " + status);	
	                	} else {
	                		var indexStatusEnd = status.indexOf(" ", indexProgress + marker.length() + 1);
	                		if (indexStatusEnd != -1) {
	                			var percent = status.substring(indexProgress + marker.length(), indexStatusEnd);
	                			sent2VecWrapper.clearCreateModelStatus();
	                			log.debug("Status der Modelerzeugung: " + percent);
	                		}
	                	} 
	                	
	                	if (finished.isFinished()) {
	                		break;
	                	} else {
	                		Thread.sleep(5 * 1000);
	                	}
	                	
	                	
	                	TransactionTemplate transactionTemplate2 = new TransactionTemplate(transactionManager);
                        transactionTemplate2.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                        transactionTemplate2.execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus status) {
                                var jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.REBUILD_SENT2VEC_MODEL);
                                if (!jobState.isShouldRun()) {
                                    log.debug("Modelerzeugung wird abgebrochen!");
                                    sent2VecWrapper.quit();
                                    jobState.setRunning(false);
                                }
                                jobState.setLastSignOfLife(new Date());
                                applicationJobStateRepository.save(jobState);
                            }
                        });    
	                	
	                	
	                }
	                
	                if (finished.getExitCode() == 0) {
	                	log.info("Model für " + language + " erzeugt");	
	                } else {
	                	log.warn("Model für " + language + " NICHT erzeugt");
	                }
	                
                }
                
                log.info("Job beendet: 'CreateSent2VecModelJob'");
            } catch (Exception ex) {
                log.error("Fehler Erzeugen des Sent2Vec-Models", ex);
            } finally {
                jobState.setRunning(false);
                jobState.setShouldRun(false);
                applicationJobStateRepository.save(jobState);
            }
        }
	}
	
	
	private static class FileWriter {
	    
        private Writer writer;
        
        private AtomicInteger errorCount = new AtomicInteger(0);

        private String filename;

        private FileWriter(String filename, OutputStream out) {
            this.filename = filename;
            writer = new OutputStreamWriter(out);
	    }
        
        public boolean writeLine(String sentence) {
            try {
                writer.write(sentence);
                writer.write("\n");
            } catch (IOException e) {
                log.error("Fehler beim Schreiben der Sätze in die Datei " + filename, e);
                if (errorCount.incrementAndGet() > 100) {
                    log.error("Das Schreiben der Sätze wird aufgrund von zu vielen Fehlern abgebrochen.");
                    return false;
                }
            }
            
            return true;
        }
	    
	    public static FileWriter create(String filename) {
	        OutputStream out = null;
            try {
                out = new FileOutputStream(filename);
            } catch (FileNotFoundException ex) {
                log.error("Fehler beim Anlegen der Datei für Sätze: " + filename, ex);
                return null;
            }
	        
	        FileWriter writer = new FileWriter(filename, out);
            
            return writer;
	    }
	    
	    public boolean close() {
	        try {
                writer.close();
            } catch (IOException ex) {
                log.error("Fehler beim Schließen der Datei für Sätze: " + filename, ex);
                return false;
            }
	        
	        return true;
	    }
	}
}