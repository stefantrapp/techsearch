package de.fernunihagen.techsearch;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import de.fernunihagen.techsearch.data.ApplicationJobState;
import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.jobs.PreprocessDocumentJob;
import de.fernunihagen.techsearch.jobs.Preprocessor;
import de.fernunihagen.techsearch.jobs.RebuildSearchIndexJob;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.services.EpoImportService;
import de.fernunihagen.techsearch.restservice.services.EpoImportServiceImpl;
import de.fernunihagen.techsearch.restservice.services.IndexSearchService;
import de.fernunihagen.techsearch.restservice.services.IndexSearchServiceImpl;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EpoImportServiceTest {

    @Autowired
    private EpoImportService epoImportService;

	@Autowired
    ApplicationContext applicationContext;
	
	public EpoImportServiceTest() {
	} 
	
	@Test
	@Ignore
	public void createAuszug() throws Exception {
	    var infile = EpoFiles.EPO_FILE_BASE_PATH + "EP3700000.txt";
	    var outfile = EpoFiles.EPO_FILE_BASE_PATH + "Auszug.txt";
	    try (FileReader fileReader = new FileReader(infile, Charset.forName("UTF-8"))) {
	        try (BufferedReader reader = new BufferedReader(fileReader)) {
	            try (FileWriter writer = new FileWriter(outfile)) {

                    var lineCount = 0;
            
                    while (lineCount < 1000) {
                        lineCount++;
                        var line = reader.readLine();
                        
                        if (line == null) {
                            break;
                        }
                        
                        writer.write(line);
                        writer.write("\n");
                    }
                    
                    writer.flush();
                    writer.close();
	            }
            }
       }
	            
	}
	
	@Test
	@Ignore
    public void testLoadAuszug() throws Exception {
	    AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        
	    
	    Map<Language, StanfordCoreNLP> stanfordCoreNLPs = new HashMap<Language, StanfordCoreNLP>();
        Language english = Language.ENGLISH;
        stanfordCoreNLPs.put(english, PreprocessDocumentJob.createPipeline(english));
	    
	    EpoImportServiceMemoryImpl epoImportService = new EpoImportServiceMemoryImpl();
	    
	    epoImportService.importEpoFile("P:\\EPO\\Auszug.txt", null, null);
	    
	    var documents = epoImportService.getDocuments();
	    
	    assertTrue(documents.size() > 0);
        
        Preprocessor p = new Preprocessor(-1, null, null, null, stanfordCoreNLPs, null);
        
        var document = documents.get(0);
        
        var processedDocument = p.processDocument(document);
        
        assertNotNull(processedDocument.getSentences());
        System.out.println(processedDocument.getSentencesAnalyzed());
        
        RebuildSearchIndexJob indexJob = new RebuildSearchIndexJobImpl(documents);
        beanFactory.autowireBean(indexJob);
        indexJob.buildIndex(null, Language.ENGLISH);
        
        IndexSearchService indexSearchService = new IndexSearchServiceTestImpl(documents);
        beanFactory.autowireBean(indexSearchService);
        
        var searchResults = indexSearchService.query(new SearchDto("quantification", Language.ENGLISH));
        assertNotNull(searchResults);
        assertTrue(searchResults.getEntries().size() > 0);
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
	
	@Test
	@Ignore
	public void testLoadAll() {
	    for (var file : EpoFiles.epoFiles) {
	        epoImportService.importEpoFile(EpoFiles.EPO_FILE_BASE_PATH + file, null, null);    
	    }
	}
	
	@Test
	@Ignore
	public void testExists() {
	    var exists = epoImportService.test();
	    assertTrue(exists);
	}
	
	@Test
	@Ignore
	public void searchPatent() throws URISyntaxException, IOException {
	    
	    var url = this.getClass().getResource("patents.txt");
	    var uri = url.toURI();
        File list = new File(uri);
        var patentNumbers = Files.readAllLines(list.toPath());

        Map<String, PatentNumber> map = new HashMap<>();
        
        patentNumbers.stream().forEach(s -> {
            map.put(s, new PatentNumber(s));
        });
        
        EpoImportServiceFilterImpl impl = new EpoImportServiceFilterImpl(map);
        
        for (var file : EpoFiles.epoFiles) {
            impl.importEpoFile(EpoFiles.EPO_FILE_BASE_PATH + file, null, null);
        }
	    
        System.out.println("########");
        for (var pn : map.values()) {
            System.out.println(pn.number + "\t" + (pn.found ? "1" : "0") );
        }
        System.out.println("########");
        
	    System.out.println(map);
	}
	
	class EpoImportServiceFilterImpl extends EpoImportServiceImpl {
	    Map<String, PatentNumber> patens;
	    
	    EpoImportServiceFilterImpl(Map<String, PatentNumber> map) {
            this.patens = map;
	        
	    }
	    
	    protected void persistPublication(Document doc, long publicationCount) {
	        var patent = patens.get(doc.getName());
	        if (patent != null) {
	            patent.found = true;
	        }
	    }
	}
	
	class PatentNumber {
	    public String number;
	    public boolean found;
	    
	    PatentNumber(String number) {
            this.number = number;
	        
	    }
	    
	}

}
