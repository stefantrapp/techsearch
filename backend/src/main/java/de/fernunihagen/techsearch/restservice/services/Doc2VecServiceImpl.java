package de.fernunihagen.techsearch.restservice.services;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.fernunihagen.techsearch.restservice.dtos.Doc2VecSearchResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Doc2VecServiceImpl implements Doc2VecService {

    private final RestTemplate restTemplate;
    
    Doc2VecServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    
    @Override
    public Doc2VecSearchResponseDto search(SearchDto search) {
        var response = restTemplate.postForObject("http://localhost:5000/api/doc2vec/search/", search, Doc2VecSearchResponseDto.class);
        return response;
    }

}
