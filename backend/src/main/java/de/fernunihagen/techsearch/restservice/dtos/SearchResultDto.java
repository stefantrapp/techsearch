package de.fernunihagen.techsearch.restservice.dtos;

import java.util.List;

public class SearchResultDto {

    private List<SearchResultEntryDto> entries;

    private List<String> word2vecWords;
    
    public List<SearchResultEntryDto> getEntries() {
        return entries;
    }

    public void setEntries(List<SearchResultEntryDto> entries) {
        this.entries = entries;
    }

    public List<String> getWord2vecWords() {
        return word2vecWords;
    }

    public void setWord2vecWords(List<String> word2vecWords) {
        this.word2vecWords = word2vecWords;
    }
    
}
