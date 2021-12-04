package de.fernunihagen.techsearch.restservice.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.data.SettingName;
import de.fernunihagen.techsearch.jobs.SentenceAnalyzer;
import de.fernunihagen.techsearch.repository.DocumentRepository;
import de.fernunihagen.techsearch.repository.SettingRepository;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchResultDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchResultEntryDto;

@Service()
public class PosTagSearchServiceImpl implements PosTagSearchService {

    @Autowired
    private IndexSearchService indexSearchService;
    
    private static Logger log = LoggerFactory.getLogger(PosTagSearchServiceImpl.class);
    
    @Autowired
    private DocumentRepository documentRepository; 
    
    @Autowired
    private SettingRepository settingRepository;
    
    private Map<Language, Set<String>> markerVerbs = new ConcurrentHashMap<Language, Set<String>>(); 
    
    public PosTagSearchServiceImpl() {
        
    }
    
    private void readMarkerVerbs(String settingsKey, Language language) {
        var eng = settingRepository.findByKey(settingsKey).getValue();
        Set<String> verbs = new HashSet<String>();
        for (var verb : eng.split(",")) {
            verbs.add(verb.trim());
        }
        markerVerbs.put(language, verbs);
    }
    
    private Set<String> getMarkerVerbs(Language language) {
        if (markerVerbs.isEmpty()) {
            readMarkerVerbs(SettingName.MARKER_VERBS_ENGLISH, Language.ENGLISH);
            readMarkerVerbs(SettingName.MARKER_VERBS_GERMAN, Language.GERMAN);    
        }
        
        return markerVerbs.get(language);
    }
    
    @Override
    public SearchResultDto search(SearchDto query) {
        var result = new SearchResultDto();
        
        var entries = new ArrayList<SearchResultEntryDto>();
        
        result.setEntries(entries);
        
        /* Schritt 1. Im Lucene-Index nach den Wörtern suchen.
         * Schritt 2. Die vom Index gefundenen Dokumente laden und dann im PoS-Model suchen. 
         */
    
        Set<String> problemMarker = getMarkerVerbs(query.getLanguage());
        
        var indexSearchDto = new SearchDto();
        indexSearchDto.setSearchTerm(query.getSearchTerm());
        var indexSearchResult = indexSearchService.query(query, new IndexQueryParameter(true, true));
        for (var indexResult : indexSearchResult.getEntries()) {
            var docId = indexResult.getDocumentId();

            var optDoc = documentRepository.findById(docId);
            if (!optDoc.isPresent()) {
                log.warn("Dokument mit der ID " + docId + " wurde nicht in der Datenbank gefunden. Möglicherweise ist der Index nicht mehr aktuell!");
            } else {
                var doc = optDoc.get();
                var analyzedSentences = doc.getSentencesAnalyzed();
                if (analyzedSentences != null) {
                    var sentences = analyzedSentences.split("\n");
                    
                    var sentModels = SentenceAnalyzer.getSentModels(sentences);
                    
                    int index = 0;
                    for (var sentModel : sentModels) {
                        
                        boolean problemFound = false;
                        
                        var grammarList = sentModel.getGrammar();
                        if (grammarList != null) {
                            
                            boolean subjectFound = false;
                            
                            for (var grammar : grammarList) {
                                if (grammar.get(1).startsWith("S")) { /* Ein Subjekt gefunden */
                                    var subject = grammar.get(0);
                                    if (subject.equals(query.getSearchTerm())) {
                                        /* Das Subjekt entspricht dem gesuchen Subjekt */
                                        subjectFound = true;
                                        break;
                                    }
                                }
                            }
                            
                            if (subjectFound) {
                                for (var grammar : grammarList) {
                                    if (grammar.get(1).startsWith("P")) { /* Ein Prädikat gefunden */
                                        var predicate  = grammar.get(0);
                                        
                                        if (problemMarker.contains(predicate)) {
                                            problemFound = true;
                                            break;
                                        }
                                    }
                                }
                            }
                         
                            if (problemFound) {
                                var entry = new SearchResultEntryDto();
                                entry.setTitel(doc.getName());
                                entry.setDocumentId(doc.getId());
                                entry.setAuszug(getSentencesAroundIndex(doc, index));
                                entries.add(entry);
                                break;
                            }
                            
                        }
                        index++;
                    }
                }
                
            }
        }
        
        
        return result;
    }
    
    private String getSentencesAroundIndex(Document doc, int index) {
        StringBuilder auszug = new StringBuilder();
        
        var allSents = doc.getSentences();
        if (allSents != null) {
            var sents = allSents.split("\n");
            
            var length = sents.length; 
            
            if (length >= index && index > 0) {
                auszug.append(sents[index-1]);
                auszug.append("\n");
            }
            
            if (length >= index + 1) {
                auszug.append(sents[index]);
                auszug.append("\n");
            }
            
            if (length >= index + 2) {
                auszug.append(sents[index+1]);
            }
        }
        
        return auszug.toString().trim();
    }
    
}
