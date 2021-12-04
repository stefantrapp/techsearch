package de.fernunihagen.techsearch.restservice.services;

import de.fernunihagen.techsearch.restservice.dtos.Doc2VecSearchResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;

public interface Doc2VecService {

    Doc2VecSearchResponseDto search(SearchDto search);
    
}
