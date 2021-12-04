package de.fernunihagen.techsearch.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import de.fernunihagen.techsearch.data.ApplicationJobEnum;
import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.jobs.Preprocessor.ProcessingStatistics;
import de.fernunihagen.techsearch.jobs.Preprocessor.Range;
import de.fernunihagen.techsearch.repository.ApplicationJobStateRepository;
import de.fernunihagen.techsearch.repository.DocumentRepository;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.StringUtils;

/**
 * Quarz-Job für die Vorverarbeitung.
 */
public class PreprocessDocumentJob implements Job {

    private static Logger log = LoggerFactory.getLogger(PreprocessDocumentJob.class);
    
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private ApplicationJobStateRepository applicationJobStateRepository;
	
	@Autowired
    private PlatformTransactionManager transactionManager;
	
	private Map<Language, StanfordCoreNLP> stanfordCoreNLPs = new HashMap<>();
	
	private ProcessingStatistics stats = new ProcessingStatistics();
	
	private long maxDocumentsToProcess = -1;
	
	private long documentToProcessAtBeginn = -1;
	
	private int minPreprocessorsCount = 2;
	
	private int minFreeCpusCount = 4;
	
	private int documentsPerPreprocessorCount = 100;
	
	private int preprocessorTimeoutSeconds = 5 * 60;
	
	private static boolean createPosModel = true;
	
	private boolean useOnlyOnePreporcessor = false;
	
	public PreprocessDocumentJob() {
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	    var jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.PREPROCESS_DOCUMENTS);
	    if (jobState.isShouldRun() && !jobState.isRunning()) {
	        try {
	            jobState.setRunning(true);
                jobState.setStartTime(new Date());
                applicationJobStateRepository.save(jobState);
	            
	            log.info("Führe Job aus: 'PreprocessDocumentJob'");
        		
        	    long lastDocumentCount = -1;
        	    
        	    stats = new ProcessingStatistics();
        	
        	    while (true) {
        	        
        	        jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.PREPROCESS_DOCUMENTS);
        	        if (!jobState.isShouldRun()) {
        	            log.info("Die Vorverarbeitung der Dokumente soll abgebrochen werden. Es wird abgebrochen.");
        	            break;
        	        }
        	        
        	        var documentCount = documentRepository.countNotPreprocessedDocuments();
            	    
            	    if (documentCount == 0) {
            	        break;
            	    } else if (lastDocumentCount == documentCount) {
            	        log.warn("Es wurden genausoviele (" + documentCount + ") Dokumente für die Vorverarbeitung gefunden, wie beim letzten Lauf. Ggf. können einige Dokumente nicht verarbeitet werden. Der Lauf wird beendet. Bitte Log prüfen.");
            	        break;
            	    }
            	    
            	    if (maxDocumentsToProcess > 0) {
            	        if (documentToProcessAtBeginn == -1) {
            	            documentToProcessAtBeginn = documentCount;
            	        }
            	        
            	        if (documentToProcessAtBeginn - documentCount >= maxDocumentsToProcess) {
            	            log.info("Für den Test wurden bereits mehr Dokument bearbeiten als maximal angegeben. Die Vorverarbeitung wird beendet.");
            	            break;
            	        }
            	    } 
            	    
            	    
            	    createPipelines();
            	    
            	    lastDocumentCount = documentCount;
            	    
            	    var preprocessors = createPreprocessors(documentCount);
                    
                    ExecutorService executorService = Executors.newFixedThreadPool(preprocessors.length);
                    
                    List<Future<String>> futures = new ArrayList<Future<String>>();
                    
                    for (int i = 0; i < preprocessors.length; i++) {
                        var future = executorService.submit(preprocessors[i]);
                        futures.add(future);
                    }
        
                    executorService.shutdown();
                    try {
                        executorService.awaitTermination(preprocessorTimeoutSeconds, TimeUnit.SECONDS);
                        
                        for (var future : futures) {
                            future.get(); /* Damit werden evtl. Exceptions geworfen, die beim Ausführen aufgetreten sind */
                        }
                    } catch (InterruptedException iex) {
                        log.error("Fehler beim Warten auf die Beendigung der Vorverarbeitung", iex);
                    }
                    
                    TransactionTemplate transactionTemplate2 = new TransactionTemplate(transactionManager);
                    transactionTemplate2.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                    transactionTemplate2.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            var jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.PREPROCESS_DOCUMENTS);
                            jobState.setLastSignOfLife(new Date());
                            applicationJobStateRepository.save(jobState);
                        }
                    });
        	    }
        	    
        	    log.info("Job beendet: 'PreprocessDocumentJob'");
	        } catch (Exception ex) {
                log.error("Fehler beim Vorverarbeiten der Dokumente", ex);
            } finally {
                jobState.setRunning(false);
                jobState.setShouldRun(false);
                applicationJobStateRepository.save(jobState);
            }
        }
	}
	
	private Preprocessor[] createPreprocessors(long documentsToProcessCount) {
        int threadCount = 1;
        
        Range[] ranges;
        
        if (documentsToProcessCount > documentsPerPreprocessorCount && !isUseOnlyOnePreporcessor()) {
            var cpuCount = Runtime.getRuntime().availableProcessors();
            threadCount = Math.max(minPreprocessorsCount, cpuCount - minFreeCpusCount);
            
            var maxDocumentsToProcess = Math.min(threadCount * documentsPerPreprocessorCount, documentsToProcessCount);
            
            ranges = divideToRanges(0, maxDocumentsToProcess, threadCount);     
        } else {
            var range = new Range();
            range.from = 0;
            range.to = Math.min(documentsPerPreprocessorCount, documentsToProcessCount);
            ranges = new Range[1];
            ranges[0] = range;
        }
         
        var preprocessors = new Preprocessor[ranges.length];
        
        for (int i = 0; i < preprocessors.length; i++) {
            preprocessors[i] = new Preprocessor(i, ranges[i], transactionManager, documentRepository, stanfordCoreNLPs, stats);
        }
        return preprocessors;
    }
    
    private Range[] divideToRanges(long from, long to, int rangeCount) {
        Range[] ranges = new Range[rangeCount];
        
        var size = (to - from) / rangeCount;
        
        for (int i = 0; i < rangeCount; i++) {
            var range = new Range();
            ranges[i] = range;
            
            if (i == 0) {
                range.from = from;
                range.to = from + size;
            } else if (i == rangeCount - 1) {
                range.from = ranges[i - 1].to;
                range.to = to;
            } else {
                range.from = ranges[i - 1].to;
                range.to = range.from + size; 
            }
        }
        
        return ranges;
    }
    

    private static Pattern nonCharacterPattern = Pattern.compile("[^ a-zA-ZßÄÖÜäöü,\\!\\?.]");
    private static Pattern numbersWithPunctation = Pattern.compile("([\\.\\,]\\d+)+");
    
    static String replaceNonCharacters(String text) {
        var result1 = numbersWithPunctation.matcher(text).replaceAll("");
        var result2 = nonCharacterPattern.matcher(result1).replaceAll("");
        var result = nonCharacterPattern.matcher(result2).replaceAll("");
        return result;
    }
    
    static class AnalyzedParts {
	    String sentencesWithPos;
        String sentences;
	    String sentencesLemmatizated;
	}
	
	private void createPipelines() {
	    /* StanfordCoreNLP ist threadsafe. Es können daher für alle Preprocessors die gleichen Instanzen verwendet werden */
        for (var language : new Language[] { Language.ENGLISH, Language.GERMAN }) {
            
            if (!stanfordCoreNLPs.containsKey(language)) {
                var pipeline = createPipeline(language);
                stanfordCoreNLPs.put(language, pipeline);
            }
        }
	}

	public static StanfordCoreNLP createPipeline(Language language) {
	    String annotators = "tokenize,ssplit,pos,lemma";
	    if (isCreatePosModel()) {
	        annotators += ",depparse";
	    }
	    
	    var props = StringUtils.argsToProperties(new String[] {"-props", Language.toString(language), "-annotators", annotators});
        var pipeline = new StanfordCoreNLP(props);
        return pipeline;
    }
	
	/**
	 * @param textContent
	 * @throws IOException
	 */
	public void getPos(String textContent) throws IOException {
		// Für deutsch: https://dev.languagetool.org/java-api
		
		Properties props = new Properties();
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    
	    CoreDocument doc = new CoreDocument(textContent);
	    // annotate
	    pipeline.annotate(doc);
	    // display sentences
	    for (CoreSentence sent : doc.sentences()) {
	        System.out.println(sent.text());
	        System.out.println(sent.posTags());
	        System.out.println(sent.lemmas());
	    }
	}

    public long getMaxDocumentsToProcess() {
        return maxDocumentsToProcess;
    }

    public void setMaxDocumentsToProcess(long maxDocumentsToProcess) {
        this.maxDocumentsToProcess = maxDocumentsToProcess;
    }

    public boolean isUseOnlyOnePreporcessor() {
        return useOnlyOnePreporcessor;
    }

    /**
     * Es soll nur ein Preporcessor verwendet werden. Nur für den Test.
     * @param useOnlyOnePreporcessor
     */
    public void setUseOnlyOnePreporcessor(boolean useOnlyOnePreporcessor) {
        this.useOnlyOnePreporcessor = useOnlyOnePreporcessor;
    }

    public static boolean isCreatePosModel() {
        return createPosModel;
    }

    /**
     * Legt fest, ob ein Pos-Model (Part-of-Speech) erzeugt werden soll. Nur für den Test kann das auf false gesetzt werden.
     * @param createPosModel
     */
    public static void setCreatePosModel(boolean createPosModel) {
        PreprocessDocumentJob.createPosModel = createPosModel;
    }
    
    
	
}