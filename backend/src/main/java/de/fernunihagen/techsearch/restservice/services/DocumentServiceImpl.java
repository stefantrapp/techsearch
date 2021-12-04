package de.fernunihagen.techsearch.restservice.services;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fernunihagen.techsearch.data.CrawlJob;
import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.data.DocumentFormat;
import de.fernunihagen.techsearch.data.DocumentType;
import de.fernunihagen.techsearch.jobs.Preprocessor;
import de.fernunihagen.techsearch.repository.DocumentRepository;
import de.fernunihagen.techsearch.restservice.dtos.DocumentDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchResultEntryDto;
import de.fernunihagen.techsearch.restservice.services.EpoImportServiceImpl.EpoLine;

@Service()
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Document getDocumentAsOriginal(int id) {
        var foundDocument = documentRepository.findById(id);
        
        if (foundDocument.isPresent()) {
            return foundDocument.get();
        }
        
        return null;
    }
    
    @Override
    public void deleteDocument(int id) {
        documentRepository.deleteById(id);
    }
    
    @Override
    public DocumentDto getDocumentAsText(int id) {
        var foundDocument = documentRepository.findById(id);
        
        if (foundDocument.isPresent()) {
            var dto = new DocumentDto();
            
            var document = foundDocument.get();
            
            if (DocumentType.EPO.equals(document.getDocumentType())) {
                var additionalInfosJson = document.getAdditionalInfos();
                ObjectMapper objectMapper = new ObjectMapper();
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, EpoLine.class);
                
                try {
                    if (additionalInfosJson != null && additionalInfosJson.length() > 0) {
                        List<EpoLine> additionalInfos = objectMapper.readValue(additionalInfosJson, type);
                        if (additionalInfos != null) {
                            for (EpoLine epoLine : additionalInfos) {
                                if ("PDFEP".equals(epoLine.getTextType())) {
                                    var pdfUrl = epoLine.getText();
                                    dto.setSourceUrl(pdfUrl);
                                    break;
                                }
                            }
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            
            if (document.getOriginalTextContent() != null && document.getOriginalTextContent().length() > 0) {
                dto.setOriginalText(document.getOriginalTextContent());
            } 
            
            var sentences = document.getSentences();
            if (sentences != null && sentences.length() > 0) {
                dto.setText(document.getSentences());
            } else {
                var originalContent = document.getOriginalContent();
                
                if (originalContent != null && originalContent.length > 0) {
                    dto.setText("Es ist kein vorverarbeiteter Textinhalt vorhanden. Binärinhalt ist vorhanden. Ggf. das Original herunterladen.");    
                } else {
                    dto.setText("Es ist kein vorverarbeiteter Textinhalt vorhanden.");
                }
                
            }
            
            return dto;
        }
        
        return null;
    }

    @Override
    public void importHtmlFile(File file, CrawlJob crawlJob) {
        Document doc = new Document();
        doc.setName(file.getName());
        doc.setDocumentType(DocumentType.CRAWLED);
        doc.setDocumentFormat(DocumentFormat.HTML);
        doc.setCrawlJob(crawlJob);
        try {
            doc.setOriginalTextContent(Files.readString(file.toPath(), StandardCharsets.UTF_8));
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        
        documentRepository.save(doc);
    }

    @Override
    public Page<Document> findDocumentByName(String name, int page, int size, String sortDir, String sort) {
        PageRequest pageReq = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sort);
        
        Page<Document> crawlJobs = documentRepository.findByNameLike(name, pageReq);
        
        return crawlJobs;
    }
    
    @Override
    public Document findDocumentById(int id) {
        var doc = documentRepository.findById(id);
        
        if (doc.isPresent()) {
            return doc.get();
        }
        
        return null;
    }

    public Document testPreporcessDocument(int id ) {
        var docOpt = documentRepository.findById(id);
        if (docOpt.isPresent()) {
            var doc = docOpt.get();
            entityManager.detach(doc);
            
            Preprocessor preprocessor = new Preprocessor(-1, null, null, null, null, null);
            preprocessor.processDocument(doc);
            return doc;
        }
        
        return null;
    }
    
    @Override
    public SearchResultEntryDto getAuszug(int id) {
        var docOpt = documentRepository.findById(id);
        
        var result = new SearchResultEntryDto();
        
        if (docOpt.isEmpty()) {
            result.setAuszug("Dokument mit der ID " + id + " ist nicht mehr vorhanden.");
            result.setDocumentId(-1);
            result.setTitel("Nicht mehr vorhanden");
        } else {
            var doc = docOpt.get();
            result.setDocumentId(doc.getId());
            result.setTitel(doc.getName());
            
            var sents = doc.getSentences();
            var auszug = sents.substring(0, Math.min(500, sents.length()));
            result.setAuszug(auszug);    
        }
                
        return result;
    }

    @Override
    public void deleteAllDocuments() {
        documentRepository.deleteAllInBatch();
    }

}
