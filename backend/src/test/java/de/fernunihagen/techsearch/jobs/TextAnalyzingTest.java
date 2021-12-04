package de.fernunihagen.techsearch.jobs;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import de.fernunihagen.techsearch.TestBase;
import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.data.DocumentFormat;
import de.fernunihagen.techsearch.data.Language;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class TextAnalyzingTest extends TestBase {

    {
        System.setProperty("tika.config", "tika-config.xml");
    }
    
    @Test
    @Ignore
    public void testAnalyzing() throws IOException {
        Map<Language, StanfordCoreNLP> stanfordCoreNLPs = new HashMap<Language, StanfordCoreNLP>();
        stanfordCoreNLPs.put(Language.GERMAN, PreprocessDocumentJob.createPipeline(Language.GERMAN));
        stanfordCoreNLPs.put(Language.ENGLISH, PreprocessDocumentJob.createPipeline(Language.ENGLISH));
        
        Preprocessor p = new Preprocessor(-1, null, null, null, stanfordCoreNLPs, null);
        
        //var text = "The casting process is interrupted.";
        //var text = "The casting yield is deteriorated";
        //var text = "The sticking layer is formed in the casting nozzle as mentioned above to cause nozzle clogging.";
        //var text = "If the sticking layer is formed in the casting nozzle as mentioned above to cause nozzle clogging, the casting process is interrupted, thereby deteriorating the casting yield.";
        //var text = "The child loves his parents.";
        //var text = "The child is buying shoes.";
        
        var text = "Der Gießvorgang wurde unterbrochen. Er versagte.";
        
        var document = new Document();
        document.setLanguage(Language.GERMAN);
        document.setOriginalTextContent(text);
        
        p.processDocument(document);
    
        var parts = p.analyze(text, Language.GERMAN);
        
        assertNotNull(parts);
        assertNotNull(parts.sentences);
        assertNotNull(parts.sentencesLemmatizated);
        assertNotNull(parts.sentencesWithPos);
        System.out.println(text);
        System.out.println("Lemma: " + parts.sentencesLemmatizated);
        System.out.println("PoS: " + parts.sentencesWithPos);
        
        text = "The casting process was interrupted. The casting yield was therefore deteriorated.";
        
        document = new Document();
        document.setLanguage(Language.ENGLISH);
        document.setOriginalTextContent(text);
        
        p.processDocument(document);
    
        parts = p.analyze(text, Language.ENGLISH);
        
        assertNotNull(parts);
        assertNotNull(parts.sentences);
        assertNotNull(parts.sentencesLemmatizated);
        assertNotNull(parts.sentencesWithPos);
        System.out.println(text);
        System.out.println("Lemma: " + parts.sentencesLemmatizated);
        System.out.println("PoS: " + parts.sentencesWithPos);
    }
    
    @Test
    @Ignore
    public void testLemma() throws IOException {
        Map<Language, StanfordCoreNLP> stanfordCoreNLPs = new HashMap<Language, StanfordCoreNLP>();
        Language lang = Language.GERMAN;
        stanfordCoreNLPs.put(lang, PreprocessDocumentJob.createPipeline(lang));
        
        Preprocessor p = new Preprocessor(-1, null, null, null, stanfordCoreNLPs, null);
        
        var text = "verunstalten, zerbricht, beschädigt, deformiert, degradiert, zerstört, verschlechtert, benachteiligt, behindert, schädigt, beeinträchtigt, zertrümmert, verdirbt, befleckt, stört, schwächt, versagt, verschlimmert";
        
        var document = new Document();
        document.setLanguage(Language.ENGLISH);
        document.setOriginalTextContent(text);
        
        p.processDocument(document);
    
        var parts = p.analyze(text, lang);
        
        assertNotNull(parts);
        assertNotNull(parts.sentences);
        assertNotNull(parts.sentencesLemmatizated);
        assertNotNull(parts.sentencesWithPos);
        System.out.println(text);
        System.out.println(parts.sentencesLemmatizated);
    }
    
    @Test
    @Ignore
    public void testAnalyzing_Word() throws IOException {
        Map<Language, StanfordCoreNLP> stanfordCoreNLPs = new HashMap<Language, StanfordCoreNLP>();
        Language english = Language.ENGLISH;
        stanfordCoreNLPs.put(english, PreprocessDocumentJob.createPipeline(english));
        
        Preprocessor p = new Preprocessor(-1, null, null, null, stanfordCoreNLPs, null);
        
        var path = Path.of("src/test/Example.docx1").toAbsolutePath();
        
        var content = Files.readAllBytes(path);
        
        var document = new Document();
        document.setOriginalContent(content);
        document.setLanguage(Language.ENGLISH);
        
        var doc = p.processDocument(document);
    
        
        
        assertNotNull(doc);
        assertNotNull(doc.getSentences());
        System.out.println(doc.getSentences());
    }
    
    @Test
    @Ignore
    public void testAnalyzing_Pdf() throws IOException {
        Map<Language, StanfordCoreNLP> stanfordCoreNLPs = new HashMap<Language, StanfordCoreNLP>();
        Language english = Language.ENGLISH;
        stanfordCoreNLPs.put(english, PreprocessDocumentJob.createPipeline(english));
        
        Preprocessor p = new Preprocessor(-1, null, null, null, stanfordCoreNLPs, null);
        
        var path = Path.of("src/test/Example.pdf").toAbsolutePath();
        
        var content = Files.readAllBytes(path);
        
        var document = new Document();
        document.setOriginalContent(content);
        document.setLanguage(Language.ENGLISH);
        
        var doc = p.processDocument(document);
        
        assertNotNull(doc);
        assertNotNull(doc.getSentences());
        System.out.println(doc.getSentences());
    }
    
    @Test
    @Ignore
    public void testAnalyzing_Html() throws IOException {
        Map<Language, StanfordCoreNLP> stanfordCoreNLPs = new HashMap<Language, StanfordCoreNLP>();
        Language english = Language.ENGLISH;
        stanfordCoreNLPs.put(english, PreprocessDocumentJob.createPipeline(english));
        
        Preprocessor p = new Preprocessor(-1, null, null, null, stanfordCoreNLPs, null);
        
        var path = Path.of("src/test/Example.html").toAbsolutePath();
        
        var content = Files.readString(path);
        
        var document = new Document();
        document.setOriginalTextContent(content);
        document.setDocumentFormat(DocumentFormat.HTML);
        document.setLanguage(Language.ENGLISH);
        
        var doc = p.processDocument(document);
    
        
        
        assertNotNull(doc);
        assertNotNull(doc.getSentences());
        System.out.println(doc.getSentences());
    }
    
    @Test
    @Ignore
    public void testLemmatization() {
        Map<Language, StanfordCoreNLP> stanfordCoreNLPs = new HashMap<Language, StanfordCoreNLP>();
        Language english = Language.ENGLISH;
        stanfordCoreNLPs.put(english, PreprocessDocumentJob.createPipeline(english));
        
        Preprocessor p = new Preprocessor(-1, null, null, null, stanfordCoreNLPs, null);
    
        var sentence = "Last year we bought some houses. This year we will buy more houses.";
        var expectedLemmatization = "last year we buy some house. this year we will buy more house.";
         
        try {
        
            var parts = p.analyze(sentence, Language.ENGLISH);
            
            assertEquals(expectedLemmatization, parts.sentencesLemmatizated);
        } catch (Exception e) {
            fail(e);
        }
    }
}

