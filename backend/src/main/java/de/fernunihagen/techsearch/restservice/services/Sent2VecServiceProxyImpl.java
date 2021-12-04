//package de.fernunihagen.techsearch.restservice.services;
//
//import java.util.List;
//
//import org.springframework.beans.factory.config.ConfigurableBeanFactory;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.context.annotation.Profile;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import de.fernunihagen.techsearch.restservice.ApiResponse;
//import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
//
//@Service()
//@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
//@Profile("!secondary")
//public class Sent2VecServiceProxyImpl implements Sent2VecService {
//
//    private final RestTemplate restTemplate;
//    
//    public Sent2VecServiceProxyImpl(RestTemplateBuilder restTemplateBuilder) {
//        this.restTemplate = restTemplateBuilder.build();
//    }
//    
//    @Override
//    public List<Sent2VecResult> search(SearchDto search) {
//        var response = restTemplate.postForObject("http://192.168.6.43:8080/api/sent2vec/search", search, Sent2VecResultApiResponse.class);
//        var result = response.getResult();
//        return result;
//    }
//    
//    /* Die Klasse ist notwendig, weil RestTemplate#postForObject keine generischen Typen verwenden kann */
//    static class Sent2VecResultApiResponse extends ApiResponse<List<Sent2VecResult>> {
//        /* leer */
//    }
//}
