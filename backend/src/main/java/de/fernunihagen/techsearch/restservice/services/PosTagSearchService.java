package de.fernunihagen.techsearch.restservice.services;

import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchResultDto;

public interface PosTagSearchService {

    public SearchResultDto search(SearchDto query);
    
}
