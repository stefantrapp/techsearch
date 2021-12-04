package de.fernunihagen.techsearch;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import de.fernunihagen.techsearch.data.ApplicationJobState;
import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.jobs.PreprocessDocumentJob;
import de.fernunihagen.techsearch.jobs.Preprocessor;
import de.fernunihagen.techsearch.jobs.RebuildSearchIndexJob;
import de.fernunihagen.techsearch.repository.DocumentRepository;
import de.fernunihagen.techsearch.restservice.services.EpoImportServiceImpl;
import de.fernunihagen.techsearch.restservice.services.IndexSearchServiceImpl;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GenerateWebsiteTest {
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    private String[] epoFiles;
    
    public GenerateWebsiteTest() {
    } 
    
    static class ValueContainer<T> {
        public T value;
        
        public ValueContainer(T initialValue) {
            value = initialValue;
        }
    }
    
    @Test
    @Ignore
    public void testGenerate2() throws Exception {
        var templateStream = getClass().getResourceAsStream("WebPageTemplate.html");
        String template = IOUtils.toString(templateStream, StandardCharsets.UTF_8);
        
        var indexPageTemplateStream = getClass().getResourceAsStream("WebPageTemplateIndex.html");
        String indexPageTemplate = IOUtils.toString(indexPageTemplateStream, StandardCharsets.UTF_8);
        
        System.out.println(template);
        
        var destinateionDir = "..\\TestWebsite\\pages";
        
        long maxPagesToGenerate = 1000;
        var pagesGeneratedCount = new ValueContainer<Integer>(0);
        
        List<PageContainer> lastPages = new ArrayList<PageContainer>();
        
        Random rnd = new Random();
        
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
    
                var docs = documentRepository.findDocumentsWithSentences(Language.ENGLISH.ordinal());
                
                var iter = docs.iterator(); 
                
                String previousFileName = null;
                
                String previousPage = null;
                
                boolean indexPageGenerated = false;
                
                while (iter.hasNext() && pagesGeneratedCount.value <= maxPagesToGenerate) {
                    var doc = iter.next();
                    var title = doc.getName();
                    var content = doc.getSentences();
                    
                    var fileName = title + ".html";
                    
                    var page = template;

                    content = StringUtils.replace(content, "\n", "<br>");
                    
                    page = StringUtils.replace(page, "{{TITLE}}", title);
                    page = StringUtils.replace(page, "{{CONTENT}}", content);
                    
                    var prev = "";
                    
                    if (previousFileName != null) {
                        prev = previousFileName;
                    }
                    
                    page = StringUtils.replace(page, "{{PREV_LINK}}", prev);
                    
                    StringBuffer randomPages = new StringBuffer();
                    
                    for (int i = 0; i <= 10 && i < lastPages.size(); i++) {
                        var randomPage = lastPages.get(rnd.nextInt(lastPages.size()));
                        randomPages.append("<a href=\"").append(randomPage.fileName).append("\">").append(randomPage.title).append("</a>").append("<br>\n");
                    }
                    
                    page = StringUtils.replace(page, "{{RANDOM_PAGES}}", randomPages.toString());
                    
                    
                    if (previousPage != null) {
                        previousPage = StringUtils.replace(previousPage, "{{NEXT_LINK}}", fileName);
                        try {
                            Files.writeString(Path.of(destinateionDir, previousFileName), previousPage, StandardOpenOption.CREATE);
                            pagesGeneratedCount.value++;
                            
                            lastPages.add(new PageContainer(fileName, title));
                            
                            if (!indexPageGenerated) {
                                
                                var indexPage = StringUtils.replace(indexPageTemplate, "{{FIRST_PAGE}}", previousFileName);
                                Files.writeString(Path.of(destinateionDir, "index.html"), indexPage, StandardOpenOption.CREATE);
                                indexPageGenerated = true;
                            }
                            
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        
                    }
                    
                    previousPage = page;
                    previousFileName = fileName;
                }
            }
        });
    }
    
    class PageContainer {
        public PageContainer(String fileName, String title) {
            this.fileName = fileName;
            this.title = title;
        }
        String fileName;
        String title;
    }
    
    @Test
    @Ignore
    public void testGenerate1() throws Exception {
        Map<Language, StanfordCoreNLP> stanfordCoreNLPs = new HashMap<Language, StanfordCoreNLP>();
        Language english = Language.ENGLISH;
        stanfordCoreNLPs.put(english, PreprocessDocumentJob.createPipeline(english));
        
        EpoImportServiceMemoryImpl epoImportService = new EpoImportServiceMemoryImpl();

        for (var epoFile : epoFiles) {
            epoImportService.importEpoFile(EpoFiles.EPO_FILE_BASE_PATH + epoFile, null, null);
            
            var documents = epoImportService.getDocuments();
            
            assertTrue(documents.size() > 0);
            
            Preprocessor p = new Preprocessor(-1, null, null, null, stanfordCoreNLPs, null);
            
            var document = documents.get(0);
            
            p.processDocument(document);
            
            for (var doc : documents) {
                System.out.println(doc.getName());
            }
        }
        
    }
    
    class IndexSearchServiceTestImpl extends IndexSearchServiceImpl {
        
        private List<Document> documents;

        IndexSearchServiceTestImpl(List<Document> documents) {
            this.documents = documents;
            
        }
        
        protected Optional<Document> findDocumentById(int docIdInt) {
            var doc = documents.stream().filter(d -> d.getId() == docIdInt).findFirst();
            
            return doc;
        }
    }
    
    class RebuildSearchIndexJobImpl extends RebuildSearchIndexJob {
        
        private List<Document> documents;

        RebuildSearchIndexJobImpl(List<Document> documents) {
            this.documents = documents;
            super.setAppendIndex(false); /* Der Test soll immer einen neuen Index anlagen */
        }
        
        protected void updateJobState(ApplicationJobState jobState) {
            /* leer */
        }
        
        protected Iterator<Document> getDocumentIterator() {
            return documents.iterator();
        }
    }
    
    class EpoImportServiceMemoryImpl extends EpoImportServiceImpl {
        
        private List<Document> documents = new ArrayList<Document>();
        
        private int docId;
        
        EpoImportServiceMemoryImpl() {
            
        }
        
        protected void persistPublication(Document doc, long publicationCount) {
            doc.setId(++docId);
            documents.add(doc);
        }

        public List<Document> getDocuments() {
            return documents;
        }
    }

}
