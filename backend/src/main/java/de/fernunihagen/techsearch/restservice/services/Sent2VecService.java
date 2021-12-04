package de.fernunihagen.techsearch.restservice.services;

import java.util.List;

import de.fernunihagen.techsearch.restservice.dtos.SearchDto;

public interface Sent2VecService {

	List<Sent2VecResult> search(SearchDto search);
	
	void quit();

}
