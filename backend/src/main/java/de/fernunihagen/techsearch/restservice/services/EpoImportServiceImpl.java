package de.fernunihagen.techsearch.restservice.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fernunihagen.techsearch.data.BulkImportJob;
import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.data.DocumentFormat;
import de.fernunihagen.techsearch.data.DocumentType;
import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.repository.DocumentRepository;


/**
 * Der Aufbau der Dateien kann dem Dokument "ep_full_text_data_for_text_analytics-user_guide_v1.4_en" aus Abschnitt 5 entnommen werden
 * 
 */

@Service
@Transactional
public class EpoImportServiceImpl implements EpoImportService {

    private static Logger log = LoggerFactory.getLogger(EpoImportServiceImpl.class);    
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private DocumentRepository documentRepository;
    
    private boolean preserveAdditionalData = true;
    
	@Override
	public void importEpoFile(String filename, BulkImportJob bulkImportJob, EpoImportCallback epoImportCallback) {
        
	    try (FileReader fileReader = new FileReader(filename, Charset.forName("UTF-8"))) {
            try (BufferedReader reader = new BufferedReader(fileReader)) {

                String line;
                String lastPublicationNumber = null;
            
                long publicationCount = 0;
                
                var completePublication = new ArrayList<EpoLine>(15); 
                
                while (true) {
                    line = reader.readLine();
                    
                    boolean fileEnd = line == null;
                    
                    String currentPublicationNumber = null;
                    EpoLine epoLine = null;
                    
                    if (!fileEnd) {
                    
                        var parts = line.split("\t");
                        
                        currentPublicationNumber = parts[1];
                        
                        epoLine = new EpoLine();
                        epoLine.setPublicationNumber(currentPublicationNumber);
                        epoLine.setPublicationKind(parts[2]);
                        epoLine.setPublicationDate(parts[3]);
                        epoLine.setLanguage(parts[4]);
                        epoLine.setTextType(parts[5]);
                        epoLine.setKey(parts[6]);
                        
                        var text = preprocessText(parts[7]);
                        
                        epoLine.setText(text);
                    }
                    
                    if ((lastPublicationNumber != null && currentPublicationNumber != null && !currentPublicationNumber.equals(lastPublicationNumber)) || fileEnd) {
                        publicationCount++;
                        
                        if (publicationCount % 1000 == 0) {
                            log.info("Publikationen gefunden: " + publicationCount);
                        }
                        
                        List<EpoLine> descriptions = completePublication.stream()
                                .filter(pub -> "DESCR".equals(pub.getTextType()))
                                .collect(Collectors.toList());
                        
                        var descriptionCount = descriptions.size();
                        
                        if (descriptionCount > 1) { /* Laut Dokumentation, gibt es höchstens eine Description */
                            var description = descriptions.get(0);
                            String publicationNumber = description.publicationNumber;
                            log.warn("Mehr als eine Beschreibung (Zeile mit TextType = 'DSCR') in Publikation gefunden. PublicationNumber: " + publicationNumber);
                        } else if (descriptionCount == 1) {
                            var description = descriptions.get(0);
                            
                            Document doc = new Document();
                            
                            if (isPreserveAdditionalData()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                var additionalInfos = objectMapper.writeValueAsString(completePublication);
                                doc.setAdditionalInfos(additionalInfos);
                            }
                            
                            List<EpoLine> claimsList = completePublication.stream()
                                .filter(pub -> "CLAIM".equals(pub.getTextType()) && pub.getLanguage().equals(description.getLanguage()))
                                .collect(Collectors.toList());
                            
                            
                            String claims = null;
                            if (claimsList != null && claimsList.size() > 0) {
                                claims = claimsList.get(0).text;
                            }
                            
                            doc.setDocumentType(DocumentType.EPO);
                            doc.setDocumentFormat(DocumentFormat.XML);
                            doc.setLanguage(Language.fromString(description.getLanguage()));
                            doc.setName(description.publicationNumber);
                            
                            String originalTextContent;
                            if (claims != null) {
                                StringBuilder textBuilder = new StringBuilder();
                                textBuilder.append(description.text);
                                textBuilder.append("\n#CLAIMS#.\n");
                                textBuilder.append(claims);
                                
                                originalTextContent = textBuilder.toString();
                            } else {
                                originalTextContent = description.text;
                            }
                            
                            doc.setOriginalTextContent(originalTextContent);
                            if (bulkImportJob != null) {
                                doc.setBulkImportJob(bulkImportJob);
                            }
                            
                            persistPublication(doc, publicationCount);
                            if (epoImportCallback != null) {
                                epoImportCallback.ImportProgess(publicationCount);
                            }
                        } else {
                            /* Nur eine Warnung. Der Fall kommt in den Dateien von EPO vor. */
                            String publicationNumber = completePublication.get(0).publicationNumber;
                            
                            log.warn("Keine Beschreibung (Zeile mit TextType = 'DSCR') in Publikation gefunden. PublicationNumber: " + publicationNumber);
                        }
                        
                        completePublication.clear();
                    }
                    
                    if (fileEnd) {
                        break;
                    }
                    
                    completePublication.add(epoLine);
                
                    lastPublicationNumber = currentPublicationNumber;
                    
//                    if (publicationCount > 500) {
//                        log.warn("Der Import wird abggebrochen, weil die maximale Anzahl von zu importieren Daten überschritten wurde.");
//                        break;
//                    }
                }
                
                log.info("Publications finnaly found: " + publicationCount);
                
            }
        } catch (Exception e) {
            log.error("Error on reading EPO-file: " + filename, e);
        }
	    
	    
	}
	
	private String preprocessText(String text) {
	    /* Damit werden Überschriften nicht Teil des nachfolgenden ersten Satzen */
	    text = text.replace("</heading>", "</heading>.\n");
        return text;
    }

    protected void persistPublication(Document doc, long publicationCount) {
	    
	    entityManager.persist(doc);
        //entityManager.detach(doc);

        if (publicationCount % 500 == 0) {
            entityManager.flush();
            entityManager.clear();
            // System.gc();
            var freeMemoryMb = Runtime.getRuntime().freeMemory() / 1024 / 1024;
            log.debug("Freier Arbeitsspeicher " + freeMemoryMb + " MB");   
        }
    }

	public boolean isPreserveAdditionalData() {
        return preserveAdditionalData;
    }

    public void setPreserveAdditionalData(boolean preserverAdditionalData) {
        this.preserveAdditionalData = preserverAdditionalData;
    }

    public static class EpoLine {
		/**
		 * seven-digit number
		 */
	    private String publicationNumber;
		
	    /* Siehe unten */
		private String publicationKind;
		
		/**
		 * format YYYY-MM-DD
		 */
		private String publicationDate;
		
		/**
		 * de, en, fr; xx means unknown
		 */
		private String language;
		
		private String textType;
		
		private String key;
		
		private String text;

        public String getPublicationNumber() {
            return publicationNumber;
        }

        public void setPublicationNumber(String publicationNumber) {
            this.publicationNumber = publicationNumber;
        }

        public String getPublicationKind() {
            return publicationKind;
        }

        public void setPublicationKind(String publicationKind) {
            this.publicationKind = publicationKind;
        }

        public String getPublicationDate() {
            return publicationDate;
        }

        public void setPublicationDate(String publicationDate) {
            this.publicationDate = publicationDate;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getTextType() {
            return textType;
        }

        public void setTextType(String textType) {
            this.textType = textType;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
		
		/* Zu publicationKind:
		   Von: https://www.epo.org/searching-for-patents/helpful-resources/first-time-here/definitions.html
		   A document: European patent application, published 18 months after filing with the EPO or 18 months after priority date 
		  
		   - A1 document
		     European patent application published with European search report
		   - A2 document
		     European patent application published without European search report (search report not available at the publication date)
		   - A3 document
		     Separate publication of the European search report
		   - A4 document
		     Supplementary search report

		   Corrected A document
           - A8 document
             Corrected title page of an A document, ie. A1 or A2 document.
           - A9 document
             Complete reprint of an A document, ie. A1, A2 or A3 document.
		 
		   B document: European patent specification

           - B1 document
             European patent specification (granted patent)
           - B2 document
             New European patent specification (amended specification)
           - B3 document
             European patent specification (after limitation procedure)

           Corrected B document

           - B8 document
             Corrected title page of a B document, ie. B1 or B2 document.
           - B9 document
             Complete reprint of a B document, ie. B1 or B2 document.

           Publication date

           Publication date of the original European patent document, ie. A1, A2, A3, B1, B2 or B3 document.
        
           Issue date (of the correction)

           Date of issue of the corrected version of a European patent document.
           W1: Supplementary correction code indicating the first (1st) corrected version of a patent document
           W2: Supplementary correction code indicating the second (2nd) corrected version of a patent document
	 	*/ 
	}

    @Override
    public boolean test() {
        return documentRepository.documentWithNameExists("0058481");
    }
	
}
