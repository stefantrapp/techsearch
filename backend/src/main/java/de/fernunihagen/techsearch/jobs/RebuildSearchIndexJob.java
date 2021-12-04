package de.fernunihagen.techsearch.jobs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import de.fernunihagen.techsearch.data.ApplicationJobEnum;
import de.fernunihagen.techsearch.data.ApplicationJobState;
import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.repository.ApplicationJobStateRepository;
import de.fernunihagen.techsearch.repository.DocumentRepository;

/**
 * Quarz-Job für die Erzeugung des Lucene-Indexes.
 */
public class RebuildSearchIndexJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(RebuildSearchIndexJob.class);
    
    @Autowired
    private IndexConfig indexConfig;
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private ApplicationJobStateRepository applicationJobStateRepository;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private boolean appendIndex;
    
    /* Document zum Hinzufügen zum Index. Die Instanz wird wiederverwendet, um Kosten beim GC zu sparen */
    private Document indexDocument = new Document();

    private Field docIdField = new StringField("docId", "", Field.Store.YES);

    public static final String ContentTextFieldName = "contents";
    
    public static final String ContentLemmaTextFieldName = "contents_lemma";
    
    private TextField docContentField = new TextField("contents", "", Store.NO);
    private TextField docContentLemmaField = new TextField(ContentLemmaTextFieldName, "", Store.NO);
    
    public RebuildSearchIndexJob() {
        indexDocument.add(docIdField);
        indexDocument.add(docContentField);
        indexDocument.add(docContentLemmaField);
    }
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.REBUILD_SEARCH_INDEX);
        if (jobState.isShouldRun() && !jobState.isRunning()) {
            try {
                jobState.setRunning(true);
                jobState.setStartTime(new Date());
                applicationJobStateRepository.save(jobState);
                
                log.info("Führe Job aus: 'RebuildSearchIndexJob'");
                
                var languages = List.of(Language.ENGLISH, Language.GERMAN);
                
                for (var language : languages) {
                    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            try {
                                buildIndex(jobState, language);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                log.error("Fehler beim Erzeugen des Suchindexes", ex);
            } finally {
                jobState.setRunning(false);
                jobState.setShouldRun(false);
                applicationJobStateRepository.save(jobState);
            }
            
            log.debug("RebuildSearchIndexJob fertig.");
        }
    }
    
    protected Iterator<de.fernunihagen.techsearch.data.Document> getDocumentIterator(Language language) {
        return documentRepository.findDocumentsWithSentences(language.ordinal()).iterator();
    }
    
    public void buildIndex(ApplicationJobState jobState, Language language) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        var indexDir = indexConfig.getLocalizedStoragePath(language);
        
        log.debug("Erzeuge Lucene-Index in Verzeichnis " + indexDir);
        Directory dir = FSDirectory.open(Path.of(indexDir));
                  
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        
        var openMode = appendIndex ? OpenMode.CREATE_OR_APPEND : OpenMode.CREATE;
        
        indexWriterConfig.setOpenMode(openMode);
        
        IndexWriter indexWriter = new IndexWriter(dir, indexWriterConfig);

        var docs = getDocumentIterator(language);                            
        
        long counter = 0;
        
        while (docs.hasNext()) {
            var doc = docs.next();
            indexDocument(indexWriter, doc);
            counter++;
            
            if (counter % 100 == 0) {
                log.debug("Füge Dokument " + doc.getId() + " " + doc.getName() + " zum Suchindex hinzu");
                updateJobState(jobState);
                entityManager.clear(); /* Somit werden die geladenen Dokumente aus dem Speicher entfernt. Sonst tritt bei vielen Dokumente in OutOfMemoryError auf */
            }
        }
        
        indexWriter.close();
    }

    private void indexDocument(IndexWriter indexWriter, de.fernunihagen.techsearch.data.Document document) throws IOException {
        String docId = Integer.toString(document.getId());
        docIdField.setStringValue(docId);
        docContentField.setStringValue(document.getSentences());
        var lemmas = document.getSentencesLemmatizated(); 
        if (lemmas != null) {
            docContentLemmaField.setStringValue(document.getSentencesLemmatizated());
        }
        
        if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
            indexWriter.addDocument(indexDocument);
        } else {
            indexWriter.updateDocument(new Term("path", docId), indexDocument);
        }
    }
    
    protected void updateJobState(ApplicationJobState jobState) {
        jobState.setLastSignOfLife(new Date());
        applicationJobStateRepository.save(jobState);
    }

    public boolean isAppendIndex() {
        return appendIndex;
    }

    public void setAppendIndex(boolean appendIndex) {
        this.appendIndex = appendIndex;
    }
}