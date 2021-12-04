package de.fernunihagen.techsearch.restservice.services;

import java.io.File;

import org.springframework.data.domain.Page;

import de.fernunihagen.techsearch.data.CrawlJob;
import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.restservice.dtos.DocumentDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchResultEntryDto;

public interface DocumentService {

    DocumentDto getDocumentAsText(int id);
    
    SearchResultEntryDto getAuszug(int id);
    
    void importHtmlFile(File file, CrawlJob crawlJob);
    
    Page<Document> findDocumentByName(String name, int page, int size, String sortDir, String sort);
    
    Document findDocumentById(int id);

    Document testPreporcessDocument(int id);
    
    Document getDocumentAsOriginal(int id);

    void deleteDocument(int id);

    void deleteAllDocuments();
    
}
