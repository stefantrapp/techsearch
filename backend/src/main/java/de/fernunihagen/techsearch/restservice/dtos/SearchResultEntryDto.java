package de.fernunihagen.techsearch.restservice.dtos;

public class SearchResultEntryDto {

    private String titel;
    
    private String auszug;
    
    private String word2VecWord;
    
    private int documentId;

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getAuszug() {
        return auszug;
    }

    public void setAuszug(String auszug) {
        this.auszug = auszug;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getWord2VecWord() {
        return word2VecWord;
    }

    public void setWord2VecWord(String word2VecWord) {
        this.word2VecWord = word2VecWord;
    }
    
}
