package de.fernunihagen.techsearch.restservice.services;

import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchResultDto;

/**
 * Interface für den Suchindex über Lucene
 *
 */
public interface IndexSearchService {
    
    public SearchResultDto query(SearchDto query);
    
    public SearchResultDto query(SearchDto query, IndexQueryParameter parameterObject);

}
