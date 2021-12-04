package de.fernunihagen.techsearch.restservice.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.fernunihagen.techsearch.restservice.ApiOkResponse;
import de.fernunihagen.techsearch.restservice.ApiResponse;
import de.fernunihagen.techsearch.restservice.dtos.DocumentBasicDto;
import de.fernunihagen.techsearch.restservice.dtos.DocumentDto;
import de.fernunihagen.techsearch.restservice.dtos.DocumentFullDto;
import de.fernunihagen.techsearch.restservice.dtos.DocumentIdDto;
import de.fernunihagen.techsearch.restservice.dtos.DocumentQueryDto;
import de.fernunihagen.techsearch.restservice.dtos.DocumentQueryResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.PageableDto;
import de.fernunihagen.techsearch.restservice.services.DocumentService;

@RestController
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/api/document/doc-orig")
    public void getFile(@RequestParam() int id, HttpServletResponse response) {
        try {
          
            var document = this.documentService.getDocumentAsOriginal(id);
            
            if (document != null) {
        
                InputStream is = null;
                var originalContent = document.getOriginalContent();
                if (originalContent != null) {
                    is = new ByteArrayInputStream(originalContent); 
                } else if (document.getOriginalTextContent() != null) {
                    is = new ByteArrayInputStream(document.getOriginalTextContent().getBytes(StandardCharsets.UTF_8)); 
                }
        
                ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                        .filename(document.getName())
                        .build();
                
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
                
                response.setContentType("application/octet-stream");
                org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            } else {
                OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
                writer.write("Dokument " + id + " nicht gefunden");
                writer.flush();
                response.flushBuffer();
            }
            
        } catch (IOException ex) {
          throw new RuntimeException("Fehler beim Download des Dokuments " + id);
        }
    }
    
    
    @PostMapping("/api/document/delete")
    public ApiResponse<String> deleteDocument(@RequestBody() DocumentIdDto documentDeleteDto) {
        var id = documentDeleteDto.getId();
        
       this.documentService.deleteDocument(id);
        
        return new ApiOkResponse();
    }
    
    @PostMapping("/api/document/delete-all")
    public ApiResponse<String> deleteDocument() {
       this.documentService.deleteAllDocuments();
        
        return new ApiOkResponse();
    }
    
    @PostMapping("/api/document/test-preprocess")
    public ApiResponse<DocumentFullDto> testPreprocessDocument(@RequestBody() DocumentIdDto documentDeleteDto) {
        var id = documentDeleteDto.getId();
        var doc = this.documentService.testPreporcessDocument(id);
        
        var result = this.modelMapper.map(doc, DocumentFullDto.class);
        
        return new ApiResponse<DocumentFullDto>(result);
    }
    
    @PostMapping("/api/document/doc")
    public ApiResponse<DocumentDto> getDocument(@RequestBody() int id) {
        var document = this.documentService.getDocumentAsText(id);
        
        return new ApiResponse<DocumentDto>(document);
    }
    
    @PostMapping("/api/document/search")
    public ApiResponse<DocumentQueryResponseDto> search(@RequestBody() DocumentQueryDto documentQueryDto) {
        var pageable = documentQueryDto.getPageable();
        
        var result = new DocumentQueryResponseDto();
        
        if (null != documentQueryDto.getName() && documentQueryDto.getName().length() > 0) {
            var page = documentService.findDocumentByName(documentQueryDto.getName(), pageable.getPage(), pageable.getSize(), pageable.getSortDir(), pageable.getSort());
            var dtos = page.stream().map(d -> modelMapper.map(d, DocumentBasicDto.class)).collect(Collectors.toList());
            result.setPageable(modelMapper.map(page, PageableDto.class));
            result.setDocuments(dtos);
        } else if (null != documentQueryDto.getId() && documentQueryDto.getId().length() > 0) {
            int id = Integer.parseInt(documentQueryDto.getId());
            var doc = documentService.findDocumentById(id);
            if (doc != null) {
                var dto = modelMapper.map(doc, DocumentBasicDto.class);
                var dtos = new ArrayList<DocumentBasicDto>();
                dtos.add(dto);
                result.setDocuments(dtos);
            }
        }
        
        return new ApiResponse<DocumentQueryResponseDto>(result);
    }
    
}
