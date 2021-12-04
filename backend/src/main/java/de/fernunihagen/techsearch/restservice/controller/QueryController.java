package de.fernunihagen.techsearch.restservice.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.fernunihagen.techsearch.restservice.ApiResponse;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchResultDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchResultEntryDto;
import de.fernunihagen.techsearch.restservice.dtos.TsneDto;
import de.fernunihagen.techsearch.restservice.dtos.TsneRequestDto;
import de.fernunihagen.techsearch.restservice.services.Doc2VecService;
import de.fernunihagen.techsearch.restservice.services.DocumentService;
import de.fernunihagen.techsearch.restservice.services.IndexQueryParameter;
import de.fernunihagen.techsearch.restservice.services.IndexSearchService;
import de.fernunihagen.techsearch.restservice.services.PosTagSearchService;
import de.fernunihagen.techsearch.restservice.services.Sent2VecService;
import de.fernunihagen.techsearch.restservice.services.Word2VecService;

@RestController
public class QueryController {

    private IndexSearchService indexSearchService;
    private Sent2VecService sent2VecService;
    private Word2VecService word2VecService;
    private Doc2VecService doc2VecService;
    private DocumentService documentService;
    private PosTagSearchService posTagSearchService; 
    

    @Autowired
    QueryController(IndexSearchService indexSearchService,
            Sent2VecService sent2VecService,
            Word2VecService word2VecService,
            Doc2VecService doc2VecService,
            DocumentService documentService,
            PosTagSearchService posTagSearchService) {
        
        this.indexSearchService = indexSearchService;
        this.sent2VecService = sent2VecService;
        this.word2VecService = word2VecService;
        this.doc2VecService = doc2VecService;
        this.documentService = documentService;
        this.posTagSearchService = posTagSearchService;
    }
    
    @PostMapping("/api/query/search-index")
    public ApiResponse<SearchResultDto> greeting(@RequestBody() SearchDto search) {
        
        var result = indexSearchService.query(search);
        
        return new ApiResponse<SearchResultDto>(result);
    }
    
    @PostMapping("/api/query/generateTsne")
    public ApiResponse<TsneDto> generateTsne(@RequestBody() SearchDto search) {
        var tsne = new TsneRequestDto();
        var searchTerm = search.getSearchTerm();
        TsneDto result;
        if (searchTerm != null && searchTerm.length() > 0) {
            var terms = search.getSearchTerm().split(", ");
            for (int i = 0; i < terms.length; i++) {
                terms[i] = terms[i].trim(); 
            }
            //new String[] {"technology", "invention", "industry", "patent"}
            tsne.setKeys(terms);
            tsne.setLanguage(search.getLanguage());
            result = word2VecService.generateTsne(tsne);
            return new ApiResponse<TsneDto>(result);
        } else {
            var response = new ApiResponse<TsneDto>();
            response.setError(true);
            response.setErrorMessage("Kein Suchterm angegeben");
            return response;
        }
    }
    
    @PostMapping("/api/query/search-pos")
    public ApiResponse<SearchResultDto> searchPos(@RequestBody() SearchDto search) {
        var results = posTagSearchService.search(search);
        
        return new ApiResponse<SearchResultDto>(results);
    }
    
    @PostMapping("/api/query/search-word2vec")
    public ApiResponse<SearchResultDto> searchWord2Vec(@RequestBody() SearchDto search) {
        var results = word2VecService.search(search);
        
        SearchResultDto response = new SearchResultDto();
        response.setWord2vecWords(results.getWords());
        
        List<SearchResultEntryDto> entries = new ArrayList<>();
        
        response.setEntries(entries);
        
        Set<Integer> docSet = new HashSet<Integer>(); 
        
        for (var result : results.getWords()) {
            var indexSearchDto = new SearchDto();
            indexSearchDto.setLanguage(search.getLanguage());
            indexSearchDto.setSearchTerm(result);
            var indexResult = indexSearchService.query(indexSearchDto);
            if (indexResult != null) {
                var indexEntries = indexResult.getEntries();
                if (indexEntries != null && indexEntries.size() > 0) {
                    var firstEntry = indexEntries.get(0);
                    
                    if (!docSet.contains(firstEntry.getDocumentId())) {
                        docSet.add(firstEntry.getDocumentId());
                        entries.add(firstEntry);
                    }
                }
            }
            
        }
        
        return new ApiResponse<SearchResultDto>(response);
    }
    
    @PostMapping("/api/query/search-doc2vec")
    public ApiResponse<SearchResultDto> searchDoc2Vec(@RequestBody() SearchDto search) {
        var results = doc2VecService.search(search);
        
        SearchResultDto response = new SearchResultDto();
        List<SearchResultEntryDto> entries = new ArrayList<>();
        response.setEntries(entries);
        
        for (var result : results.getDocuments()) {
            var docId = Integer.parseInt(result);
            var searchResultEntryDto = documentService.getAuszug(docId);
            entries.add(searchResultEntryDto);
        }
        
        return new ApiResponse<SearchResultDto>(response);
    }
    
    @PostMapping("/api/query/search-sent2vec")
    public ApiResponse<SearchResultDto> searchSent2vec(@RequestBody() SearchDto search) {
        var results = sent2VecService.search(search);
        
        SearchResultDto response = new SearchResultDto();
        
        List<SearchResultEntryDto> entries = new ArrayList<>();
        
        response.setEntries(entries);
        
        Set<Integer> docSet = new HashSet<Integer>(); 
        
        for (var result : results) {
            var indexSearchDto = new SearchDto();
            indexSearchDto.setLanguage(search.getLanguage());
            
            var sentence = result.getSentence();
            
            /* Kleine Wörter filtern. Es dauert sonst zu lange über den Index zu suchen. */
            
            var parts = new ArrayList<String>(Arrays.asList(sentence.split(" ")));
            parts.removeIf(s -> s.length() < 4);
            var newSent = String.join(" ", parts);
            
            indexSearchDto.setSearchTerm(newSent);
            var indexResult = indexSearchService.query(search, new IndexQueryParameter(true, false, 5));
            if (indexResult != null) {
                var indexEntries = indexResult.getEntries();
                if (indexEntries != null && indexEntries.size() > 0) {
                    var firstEntry = indexEntries.get(0);
                    
                    var docId = firstEntry.getDocumentId();
                    
                    if (!docSet.contains(docId)) {
                        docSet.add(docId);
                        
                        var searchResultEntryDto = documentService.getAuszug(docId);
                        
                        entries.add(searchResultEntryDto);
                    }
                }
            }
        }
        
        return new ApiResponse<SearchResultDto>(response);
    }
}
