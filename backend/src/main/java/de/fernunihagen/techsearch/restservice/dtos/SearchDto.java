package de.fernunihagen.techsearch.restservice.dtos;

import de.fernunihagen.techsearch.data.Language;

public class SearchDto {
    
    private String searchTerm;
    
    private Language language;
    
    public SearchDto() {
    }
    
    public SearchDto(String searchTerm, Language language) {
        this.searchTerm = searchTerm;
        this.language = language;
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	} 
}
