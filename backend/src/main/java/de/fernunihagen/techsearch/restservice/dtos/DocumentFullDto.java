package de.fernunihagen.techsearch.restservice.dtos;

public class DocumentFullDto extends DocumentBasicDto {

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

    private String sentences;
    
    private String SentencesLemmatizated;
    
    private String sentencesAnalyzed;
    
}
