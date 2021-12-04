package de.fernunihagen.techsearch.restservice.dtos;

public class SearchTestDto {
    
    private String searchTerm;
            
    private int maxResults;

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
}
