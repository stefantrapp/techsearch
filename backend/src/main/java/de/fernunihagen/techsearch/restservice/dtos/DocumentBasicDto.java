package de.fernunihagen.techsearch.restservice.dtos;

import de.fernunihagen.techsearch.data.DocumentFormat;
import de.fernunihagen.techsearch.data.DocumentType;
import de.fernunihagen.techsearch.data.Language;

public class DocumentBasicDto {

    public long getOriginalBinaryLength() {
        return originalBinaryLength;
    }

    public void setOriginalBinaryLength(long originalBinaryLength) {
        this.originalBinaryLength = originalBinaryLength;
    }

    public long getOriginalTextLength() {
        return originalTextLength;
    }

    public void setOriginalTextLength(long originalTextLength) {
        this.originalTextLength = originalTextLength;
    }

    public long getSentenceLength() {
        return sentenceLength;
    }

    public void setSentenceLength(long sentenceLength) {
        this.sentenceLength = sentenceLength;
    }

    public DocumentFormat getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(DocumentFormat documentFormat) {
        this.documentFormat = documentFormat;
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

    private int id;
    
    private String name;
    
    private String text;
    
    private long originalBinaryLength;
    
    private long originalTextLength;
    
    private long sentenceLength;
    
    private DocumentFormat documentFormat;
    
    private DocumentType documentType;
    
    private Language language; 
    
    private String sourceUrl;
    
    /* Wird auf dem Client gesetzt, wenn ein Crawljob gelöscht wird */
    private boolean deleted;
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
}
