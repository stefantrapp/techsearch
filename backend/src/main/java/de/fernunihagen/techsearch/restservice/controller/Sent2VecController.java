package de.fernunihagen.techsearch.restservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.fernunihagen.techsearch.restservice.ApiResponse;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.services.Sent2VecResult;
import de.fernunihagen.techsearch.restservice.services.Sent2VecService;

@RestController
public class Sent2VecController {

	@Autowired
	private Sent2VecService sent2VecService;
	
    @PostMapping("/api/sent2vec/search")
    public ApiResponse<List<Sent2VecResult>> search(@RequestBody() SearchDto search) {
    	var searchResults = sent2VecService.search(search);

        return new ApiResponse<List<Sent2VecResult>>(searchResults);
    }
    
}
