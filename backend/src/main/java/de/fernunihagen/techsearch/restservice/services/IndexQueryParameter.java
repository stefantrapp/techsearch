package de.fernunihagen.techsearch.restservice.services;

public class IndexQueryParameter {
    
    public boolean returnOnlyDocumentIds;
    
    public boolean searchInLemmas;
    
    public int maxResults = 50;

    public IndexQueryParameter(boolean returnOnlyDocumentIds, boolean searchInLemmas) {
        this.returnOnlyDocumentIds = returnOnlyDocumentIds;
        this.searchInLemmas = searchInLemmas;
    }
    
    public IndexQueryParameter(boolean returnOnlyDocumentIds, boolean searchInLemmas, int maxResults) {
        this.returnOnlyDocumentIds = returnOnlyDocumentIds;
        this.searchInLemmas = searchInLemmas;
        this.maxResults = maxResults;
    }
}