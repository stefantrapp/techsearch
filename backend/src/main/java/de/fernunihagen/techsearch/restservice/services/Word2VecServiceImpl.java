package de.fernunihagen.techsearch.restservice.services;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.dtos.TsneDto;
import de.fernunihagen.techsearch.restservice.dtos.TsneRequestDto;
import de.fernunihagen.techsearch.restservice.dtos.Word2VecSearchResponseDto;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Word2VecServiceImpl implements Word2VecService {

    private final RestTemplate restTemplate;
    
    public Word2VecServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    
    @Override
    public Word2VecSearchResponseDto search(SearchDto search) {
        var response = restTemplate.postForObject("http://localhost:5000/api/word2vec/search/", search, Word2VecSearchResponseDto.class);
        return response;
    }

    @Override
    public TsneDto generateTsne(TsneRequestDto search) {
        var response = restTemplate.postForObject("http://localhost:5000/api/word2vec/tsne/", search, TsneDto.class);
        return response;
    }
  
}
