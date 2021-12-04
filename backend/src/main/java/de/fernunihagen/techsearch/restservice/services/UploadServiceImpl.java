package de.fernunihagen.techsearch.restservice.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.data.DocumentFormat;
import de.fernunihagen.techsearch.data.DocumentType;
import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.repository.DocumentRepository;
import de.fernunihagen.techsearch.restservice.dtos.UploadDataDto;

@Service
public class UploadServiceImpl implements UploadService {

	DocumentRepository documentRepository;

	public UploadServiceImpl(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}
	
	@Override
	public void upload(MultipartFile file, UploadDataDto uploadData) {
		
		Document doc = new Document();
		var fileName = file.getOriginalFilename();
		doc.setName(fileName);
		
		var language = uploadData.getLanguage();
		if (language != null) {
		    if (language.equals("ENGLISH")) {
		        doc.setLanguage(Language.ENGLISH);
		    } else if (language.equals("GERMAN")) {
		        doc.setLanguage(Language.GERMAN);    
		    } else {
		        doc.setLanguage(Language.UNKNOWN);  
		    }
		}
		
		var fileNameLower = fileName.toLowerCase();
		
		try {
		    doc.setDocumentType(DocumentType.UPLOADED);
		    
		    var bytes = file.getBytes();

		    var dotIndex = fileNameLower.lastIndexOf(".");
            if (dotIndex != -1) {
        	    var suffix = fileNameLower.substring(dotIndex);
                var docFormat = DocumentFormat.fromSuffix(suffix);
                doc.setDocumentFormat(docFormat);
            }
		    
	        if (fileNameLower.endsWith(".txt") 
	                || fileNameLower.endsWith(".xml")
	                || fileNameLower.endsWith(".htm")
	                || fileNameLower.endsWith(".html")) {
	            String str = new String(bytes, StandardCharsets.UTF_8);
	            
            
	            doc.setOriginalTextContent(str);  
	        } else {
	            doc.setOriginalContent(bytes);    
	        }
	        
	            
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
		
		documentRepository.save(doc);
		
//		try (InputStream in = file.getInputStream()) {
//			try (FileOutputStream out = new FileOutputStream("Y:/temp/out.txt")) {
//				byte[] buffer = new byte[1024];
//				
//				int count = 0;
//				while ((count = in.read(buffer)) != -1) {
//					out.write(buffer, 0, count);
//				}
//			} 
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
		
	}

}
