package de.fernunihagen.techsearch.restservice.services;

import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.dtos.TsneDto;
import de.fernunihagen.techsearch.restservice.dtos.TsneRequestDto;
import de.fernunihagen.techsearch.restservice.dtos.Word2VecSearchResponseDto;

public interface Word2VecService {

    Word2VecSearchResponseDto search(SearchDto search);
    
    TsneDto generateTsne(TsneRequestDto search);

}
