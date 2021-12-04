package de.fernunihagen.techsearch.jobs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.xml.XMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.languagetool.tagging.de.GermanTagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.data.DocumentFormat;
import de.fernunihagen.techsearch.data.DocumentType;
import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.jobs.PreprocessDocumentJob.AnalyzedParts;
import de.fernunihagen.techsearch.repository.DocumentRepository;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Klasse, die ein Dokument vorverarbeitet.
 */
public class Preprocessor implements Callable<String> {

    private int number;
    private Range range;
    private PlatformTransactionManager transactionManager;
    private DocumentRepository documentRepository;
    private Map<Language, StanfordCoreNLP> stanfordCoreNLPs;
    private Preprocessor.ProcessingStatistics stats;
    
    private static Object statsLock = new Object();
    
    private static Tika tika = new Tika();
    
    private static Logger log = LoggerFactory.getLogger(PreprocessDocumentJob.class);
    
    public static boolean createPosModel = true;
    
    private static GermanTagger germanTagger = new GermanTagger();
    
    public Preprocessor(int number, Range range, 
            PlatformTransactionManager transactionManager, 
            DocumentRepository documentRepository, 
            Map<Language, StanfordCoreNLP> stanfordCoreNLPs, Preprocessor.ProcessingStatistics stats) {
        this.number = number;
        this.range = range;
        this.transactionManager = transactionManager;
        this.documentRepository = documentRepository;
        this.stanfordCoreNLPs = stanfordCoreNLPs;
        
        if (stats == null) { /* Falls der Preprocessor einzeln oder in einem Test verwendet wird. */
            stats = new ProcessingStatistics();
        }
        
        this.stats = stats;
    }
    
    @Override
    public String call() throws Exception {
        log.debug("Dokumentenvorverabeitung #" + number + " Thread " + Thread.currentThread().getName() + " Dokumente werden verarbeite: von " + range.from + " bis " + range.to);

        Preprocessor.CountWrapper countWrapper = new CountWrapper();
    
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
    
                try {
                    long offset = range.from;
                    long limit = range.to - range.from;
                    var documents = documentRepository.findNotPreprocessedDocumentsFromTo(offset, limit);
                    
                    documents.forEach(document -> {
                        countWrapper.count++;
                        
                        var processedDocument = processDocument(document);
                        
                        if (processedDocument != null) { 
                            documentRepository.save(document);
                        }
                    });
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            
        });
        
        return null;
    }
    
    private String getContentWithTika(InputStream byteStream, String docLogInfo) {
        try {
            String textContent = tika.parseToString(byteStream);
            byteStream.close();
            
            return textContent;
        } catch (Exception e) {
            log.error("Fehler bei der Textextraktion aus dem Dokument " + docLogInfo, e);
            return null;
        }
    }
    
    private ContentWrapper getContentFromHtml(String htmlContent, String docLogInfo) {
        ParseContext parseContext = new ParseContext();
        HtmlParser xmlParser = new HtmlParser();
        
        BodyContentHandler handler = new BodyContentHandler(-1); /*-1 Bedeutet kein Limit bei der Größe der Ausgabe */
        Metadata metadata = new Metadata();
        
        try (InputStream stream = new ByteArrayInputStream(htmlContent.getBytes())) {
            xmlParser.parse(stream, handler, metadata, parseContext);
            
            var contentWrapper = new ContentWrapper(); 
            var title = metadata.get(TikaCoreProperties.TITLE);
            metadata.get(TikaCoreProperties.LANGUAGE);
            
            var content = handler.toString();
            
            if (title != null) {
                content = title + " " + content;
            }
            
            contentWrapper.textContent = content; 
            contentWrapper.metadata = metadata;
            return contentWrapper;
        } catch(Exception e) {
            log.error("Dokument konnte nicht als XML geparsed werden: " + docLogInfo, e);
            return null;
        }
    }
    
    private ContentWrapper getContentFromXml(String xmlContent, String docLogInfo) {
        ParseContext parseContext = new ParseContext();
        XMLParser xmlParser = new XMLParser();
        
        BodyContentHandler handler = new BodyContentHandler(-1); /*-1 Bedeutet kein Limit bei der Größe der Ausgabe */
        Metadata metadata = new Metadata();
        
        try (InputStream stream = new ByteArrayInputStream(xmlContent.getBytes())) {
            xmlParser.parse(stream, handler, metadata, parseContext);
            
            var contentWrapper = new ContentWrapper(); 
            var title = metadata.get(TikaCoreProperties.TITLE);
            
            var content = handler.toString();
            
            if (title != null) {
                content = title + " " + content;
            }
            
            contentWrapper.textContent = content; 
            contentWrapper.metadata = metadata;
            return contentWrapper;
        } catch(Exception e) {
            log.error("Dokument konnte nicht als XML geparsed werden: " + docLogInfo, e);
            return null;
        }
    }
    
    static class CountWrapper {
        public int count;
    }
    
    class ContentWrapper {
        Metadata metadata;
        String textContent;
    }
    
    AnalyzedParts analyze(String textContent, Language language) throws IOException {

        StanfordCoreNLP pipeline = stanfordCoreNLPs.get(language);
        
        var analyzedParts = new AnalyzedParts();
        
        CoreDocument doc = new CoreDocument(textContent);
        pipeline.annotate(doc);
        

        var sentences = doc.sentences();
                
        var builderNormal = new StringBuilder();
        var builderLemma = new StringBuilder();
        var builderGrammar = new StringBuilder();
        
        for (CoreSentence coreSentence : sentences) {
            var text = coreSentence.text();
            
            /* Zeilenumbrüche entfernen, da die Zeilen in einer einzigen Spalte in der Datenbank liegen.
             * Das Modell ist damit einfacher zu erzeugen */
            text = text.replace("\n", "");
            text = text.replace("\r", "");
            
            builderNormal.append(text).append("\n");
            
            StringBuilder lemmaSentenc;
            if (language == Language.GERMAN) {
                lemmaSentenc = coreSentence.tokens().stream().collect(StringBuilder::new, (b, t ) -> {
                    var lemma = SentenceAnalyzer.getLemma(t, germanTagger);
                    b.append(lemma).append(t.after());
                }, StringBuilder::append);
            } else {
                lemmaSentenc = coreSentence.tokens().stream().collect(StringBuilder::new, (b, t ) -> {
                    b.append(t.lemma()).append(t.after());
                }, StringBuilder::append);
            }
            
            builderLemma.append(lemmaSentenc).append("\n");
            
            if (createPosModel) {
                var sentenceModel = SentenceAnalyzer.getModel(coreSentence, language == Language.GERMAN ? germanTagger : null);
                builderGrammar.append(sentenceModel).append("\n");
            }
        }
        
        analyzedParts.sentences = builderNormal.toString().trim();
        analyzedParts.sentencesLemmatizated = builderLemma.toString().trim();
        analyzedParts.sentencesWithPos = builderGrammar.toString().trim();
        
        return analyzedParts;
    }

    public Document processDocument(Document document) {
        String docLogInfo = document.getName() +  ". ID:" + document.getId();
        
        log.debug("Vorverarbeitung des Dokuments: " + docLogInfo);
        
        String plainTextContent = null;
        
        InputStream byteStream = null;
        
        byte[] binaryContent = document.getOriginalContent();
        
        if (binaryContent != null) {
            log.debug("Dokument hat Binärinhalt. Es wird versucht, den Text zu extrahieren. " + docLogInfo);
            byteStream = new ByteArrayInputStream(binaryContent);
            plainTextContent = getContentWithTika(byteStream, docLogInfo);
            plainTextContent = plainTextContent.replace("\n", " ");
        } else {
            String textContent = document.getOriginalTextContent();
            if (textContent != null) {
                if (document.getDocumentType() == DocumentType.EPO) {
                    textContent = "<doc>" + textContent + "</doc>";
                }
                
                if (document.getDocumentFormat() == DocumentFormat.XML) {
                    var contentWrapper = getContentFromXml(textContent, docLogInfo);
                    if (contentWrapper != null) {
                        plainTextContent = contentWrapper.textContent;
                        plainTextContent = plainTextContent.replace("\n", " ");
                        if (document.getLanguage() == null ||  document.getLanguage() == Language.UNKNOWN) {
                            String lang = contentWrapper.metadata.get(TikaCoreProperties.LANGUAGE);
                            var language = Language.fromString(lang);
                            
                            if (language == Language.UNKNOWN) {
                                var indexGermanLng = textContent.indexOf("lang=\"de\"");
                                var indexEnglishLng = textContent.indexOf("lang=\"en\"");
                                
                                if (indexGermanLng != -1 && indexEnglishLng != -1) {
                                    if (indexGermanLng < indexEnglishLng) {
                                        language = Language.GERMAN;    
                                    } else {
                                        language = Language.ENGLISH;
                                    }
                                } else if (indexGermanLng != -1) {
                                    language = Language.GERMAN; 
                                } else if (indexEnglishLng != -1) {
                                    language = Language.ENGLISH; 
                                }
                            }
                            
                            document.setLanguage(language);
                        }
                    }
                }
                
                if (document.getDocumentFormat() == DocumentFormat.HTML) {
                    var contentWrapper = getContentFromHtml(textContent, docLogInfo);
                    if (contentWrapper != null) {
                        plainTextContent = contentWrapper.textContent;
                        plainTextContent = plainTextContent.replace("\n", " ");
                        
                        if (document.getLanguage() == null ||  document.getLanguage() == Language.UNKNOWN) {
                            String lang = contentWrapper.metadata.get(TikaCoreProperties.LANGUAGE);
                            
                            if (lang == null) {
                                lang = contentWrapper.metadata.get("Content-Language");
                            }
                            
                            if (lang != null) {
                                var indexOfDash = lang.indexOf("-");
                                if (indexOfDash != -1) {
                                    lang = lang.substring(0, indexOfDash);    
                                } else {
                                    var langLower = lang.toLowerCase();
                                    if (langLower.contains("de")) {
                                        lang = "de";
                                    } else if (langLower.contains("en")) {
                                        lang = "en";
                                    }
                                }
                            }
                        
                            var language = Language.fromString(lang);
                            
                            document.setLanguage(language);
                        }
                    }
                }
                
                if (plainTextContent == null) {
                    try {
                        byteStream = new ByteArrayInputStream(textContent.getBytes());
                        plainTextContent = getContentWithTika(byteStream, docLogInfo);
                        plainTextContent = plainTextContent.replace("\n", " ");
                    } catch (Exception ex) {
                        log.error("Text konnte aus Dokument nicht extrahiert werden " + docLogInfo + ". Inhalt: " + textContent , ex);
                    }
                }
            } else {
                log.debug("Dokument hat keine Binär- und keinen Textinhalt!" + docLogInfo);
            }
        }
        
        if (plainTextContent != null) {
            try {
                var strippendPlainTextContent = PreprocessDocumentJob.replaceNonCharacters(plainTextContent);
                
                var language = document.getLanguage();
                
                if (stanfordCoreNLPs == null) {
                    /* Der Preprocessor wird wohl einzlen Verwendet */
                    stanfordCoreNLPs = new HashMap<>(); 
                    
                    if (language == Language.ENGLISH || language == Language.GERMAN) {
                        var pipeline = PreprocessDocumentJob.createPipeline(language);
                        stanfordCoreNLPs.put(language, pipeline);
                    }                        
                }
                
                if (!stanfordCoreNLPs.containsKey(language)) {
                    /* Die Sprache wird nicht unterstützt. Deshalb die Felder auf einen String setzen, damit sie nicht nocheinmal verarbeitet werden */
                    document.setSentences(" ");
                    document.setSentencesLemmatizated(" ");
                    document.setSentencesAnalyzed(" ");
                } else {
                    var analyzedParts = analyze(strippendPlainTextContent, document.getLanguage());
                    
                    document.setSentences(analyzedParts.sentences);
                    document.setSentencesLemmatizated(analyzedParts.sentencesLemmatizated);
                    document.setSentencesAnalyzed(analyzedParts.sentencesWithPos);
                }
                
                printStats();
                
                return document;
            } catch (Exception e) {
                log.error("Fehler bei der Textextraktion des Dokuments: " + docLogInfo, e);
            }   
        } else {
            log.info("Kein Plaintext für Dokument gefunden: " + docLogInfo);
        }
        
        return null;
    }
    
    private void printStats() {
        synchronized (statsLock) {
            stats.processedDocuments++;
            if (stats.start == 0) { /* Erst hier den Startpunkt feststellen. Dann werden evtl. Initialisierungen nicht mitgerechnet. */
                stats.start = System.currentTimeMillis();
            }
        }

        if (stats.processedDocuments % 100 == 0) {
            long now = System.currentTimeMillis();
            long durationMillis = now - stats.start;
            
            PeriodFormatter formatter = new PeriodFormatterBuilder().
                    appendDays().appendSuffix("d")
                    .appendHours().appendSuffix("h")
                    .appendMinutes().appendSuffix("m")
                    .appendSeconds().appendSuffix("s").toFormatter();

            String formatedDuraction = formatter.print(new Duration(durationMillis).toPeriod());
            
            var durationForOneDocument = durationMillis / stats.processedDocuments;

            log.debug("Statistik: Verarbeitete Dokumente: " + stats.processedDocuments + " Dauer: " + formatedDuraction + ". Durchnittliche Dauer für ein Dokument " + durationForOneDocument + " ms");
        }
    }

    static class ProcessingStatistics {
        long start;
        long processedDocuments;
    }
    
    static class Range {
        public long from;
        public long to;
    }
}