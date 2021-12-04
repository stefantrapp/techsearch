package de.fernunihagen.techsearch.restservice.services;

import java.util.Collection;

public interface QueryService {

    Collection<String> query(String query);
    
}
