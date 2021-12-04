package de.fernunihagen.techsearch.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Klasse für ein Dokument
 */

@Entity
@Table(indexes = @Index(name = "crawl_job_id_index", columnList = "crawl_job_id"))
public class Document {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private int id;
	
	/**
	 * Der originale Binärinhalte einer Datei. Nur gefüllt, wenn es keine Text-Datei (XML, HTML, TXT)
	 */
	private byte[] originalBinaryContent;
	
	/**
	 * Der originale Textinhalt einer Datei.
	 */
	@Column(columnDefinition = "TEXT NULL")
	private String originalTextContent;
	
	/**
	 * Der Name eines Dokuments. Wird ggf. von den Metadaten abgeleitet. 
	 */
	private String name;

	private Language language;
	
	/**
	 * Zusätliche Informationen zum Dokument. Dort werden bei EPO-Dateien die alle Informationen zu einer Publikation abgelegt.
	 */
	@Column(columnDefinition = "TEXT NULL")
	private String additionalInfos;
	
	@Column(columnDefinition = "TEXT NULL")
	private String processedContent;
	
	/**
	 * Der Type des Dokuments.
	 */
	private DocumentType documentType;
	
	/**
	 * Das Format des Dokuments.
	 */
	private DocumentFormat documentFormat;
	
	/**
	 * Die Sätzen eines Dokuments nach der Vorverarbeitung. Jeder Satz steht in einer neuen Zeile.
	 */
	@Column(columnDefinition = "TEXT NULL")
	private String sentences;
	
	/**
	 * Die lemmatisierten Sätzere eines Dokuments nach der Vorverarbeitung. Jeder Satz steht in einer neuen Zeile.
	 */
	@Column(columnDefinition = "TEXT NULL")
	private String SentencesLemmatizated;
	
	/**
	 * Die nach Pars-of-Speech und Grammatik analysierten Sätze eines Dokument nach der Vorverarbeitung.
	 * Für jedes Satz steht die Analyse in einer eigenen Zeile. Für das Format sieh die Klasse "SentModel"
	 * @see de.fernunihagen.techsearch.jobs.SentenceAnalyzer.SentModel
	 * 
	 */
	@Column(columnDefinition = "TEXT NULL")
	private String sentencesAnalyzed;
	
	/**
	 * Der Crawl-Job, durch den dieses Dokument erzeugt wurde.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private CrawlJob crawlJob;
	
	/**
	 * Der BulkImportJob, durch den dieses Dokument erzeugt wurde.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
    private BulkImportJob bulkImportJob;
	
	public byte[] getOriginalContent() {
		return originalBinaryContent;
	}

	public void setOriginalContent(byte[] originalContent) {
		this.originalBinaryContent = originalContent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getAdditionalInfos() {
        return additionalInfos;
    }

    public void setAdditionalInfos(String additionalInfos) {
        this.additionalInfos = additionalInfos;
    }

    public String getProcessedContent() {
        return processedContent;
    }

    public void setProcessedContent(String processedContent) {
        this.processedContent = processedContent;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getOriginalTextContent() {
        return originalTextContent;
    }

    public void setOriginalTextContent(String originalTextContent) {
        this.originalTextContent = originalTextContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DocumentFormat getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(DocumentFormat documentFormat) {
        this.documentFormat = documentFormat;
    }

    public String getSentences() {
        return sentences;
    }

    public void setSentences(String sentences) {
        this.sentences = sentences;
    }

    public String getSentencesLemmatizated() {
        return SentencesLemmatizated;
    }

    public void setSentencesLemmatizated(String sentencesLemmatizated) {
        SentencesLemmatizated = sentencesLemmatizated;
    }

    public String getSentencesAnalyzed() {
        return sentencesAnalyzed;
    }

    public void setSentencesAnalyzed(String sentencesAnalyzed) {
        this.sentencesAnalyzed = sentencesAnalyzed;
    }

    public CrawlJob getCrawlJob() {
        return crawlJob;
    }

    public void setCrawlJob(CrawlJob crawlJob) {
        this.crawlJob = crawlJob;
    }

    public BulkImportJob getBulkImportJob() {
        return bulkImportJob;
    }

    public void setBulkImportJob(BulkImportJob bulkImportJob) {
        this.bulkImportJob = bulkImportJob;
    }

    
}
