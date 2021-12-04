package de.fernunihagen.techsearch.restservice.services;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fernunihagen.techsearch.jobs.IndexConfig;
import de.fernunihagen.techsearch.jobs.RebuildSearchIndexJob;
import de.fernunihagen.techsearch.repository.DocumentRepository;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchResultDto;
import de.fernunihagen.techsearch.restservice.dtos.SearchResultEntryDto;

@Service
public class IndexSearchServiceImpl implements IndexSearchService {

    /**
     * Das ist die Länge des Auszugs (des "Snippets"), eines Dokument bei einem Treffer. 
     */
    private int FRAGMENT_SIZE = 200;
    
    private int MAX_FRAGEMENTS_PER_DOC = 5;
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private IndexConfig indexConfig;
    
    @Override
    public SearchResultDto query(SearchDto search) {
        return query(search, new IndexQueryParameter(false, false, 50));
    }
    
    @Override
    public SearchResultDto query(SearchDto search, IndexQueryParameter parameterObject) {
        try {
            String fieldName;
            if (parameterObject.searchInLemmas) {
                fieldName = RebuildSearchIndexJob.ContentLemmaTextFieldName;
            } else {
                fieldName = RebuildSearchIndexJob.ContentTextFieldName;
            }                          
            
            var searchResultDto = new SearchResultDto();
            var searchResultEntryDtos = new ArrayList<SearchResultEntryDto>();
            searchResultDto.setEntries(searchResultEntryDtos);
            
            var indexDir = indexConfig.getLocalizedStoragePath(search.getLanguage());
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser(fieldName, analyzer);
            
            var searchTerm = search.getSearchTerm();
            
            if (searchTerm == null) {
                return searchResultDto;
            }
            
            Query query = parser.parse(search.getSearchTerm());
            TopDocs topDocs = searcher.search(query, parameterObject.maxResults);
            
            

            Formatter formatter = new SimpleHTMLFormatter("<span class=\"matched-text\">", "</span>");
            QueryScorer queryScorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter, queryScorer);
            
            highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, FRAGMENT_SIZE));
            highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
            
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (var hit : hits) {
                Document doc = searcher.doc(hit.doc);
                String docId = doc.get("docId");
            
                var docIdInt = Integer.parseInt(docId);
                var searchResultEntryDto = new SearchResultEntryDto();
                
                if (parameterObject.returnOnlyDocumentIds) {
                    searchResultEntryDto.setDocumentId(docIdInt);
                } else {
                    var document = findDocumentById(docIdInt);
                    
                    String highlightedText;
                    if (document.isPresent()) {
                        String textContent; 
                        if (parameterObject.searchInLemmas) {
                            textContent = document.get().getSentencesLemmatizated();
                        } else {
                            textContent = document.get().getSentences();
                        }
                        
                        //highlightedText = highlighter.getBestFragment(analyzer, "contents", textContent);
                        
                        TokenStream tokenStream = analyzer.tokenStream(fieldName, textContent);
                        var bestFragements = highlighter.getBestTextFragments(tokenStream, textContent, false, MAX_FRAGEMENTS_PER_DOC);
                        
                        var textBuffer = new StringBuffer();
                        
                        for (var bestFragement  : bestFragements) {
                            var scor = bestFragement.getScore();
                            if (scor < 1) {
                                break;
                            } else {
                                textBuffer.append(bestFragement.toString());
                            }
                        }
                        
                        highlightedText = textBuffer.toString();
                        
                        searchResultEntryDto.setDocumentId(docIdInt);
                        searchResultEntryDto.setTitel(document.get().getName());
                    } else {
                        highlightedText = "Dokument mit der ID " + docIdInt + " ist nicht mehr vorhanden.";
                        searchResultEntryDto.setDocumentId(-1);
                        searchResultEntryDto.setTitel("Nicht mehr vorhanden");
                    }
                    
                    searchResultEntryDto.setAuszug(highlightedText);
                }
                searchResultEntryDtos.add(searchResultEntryDto);
            }
            
            return searchResultDto;
            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected Optional<de.fernunihagen.techsearch.data.Document> findDocumentById(int docIdInt) {
        return documentRepository.findById(docIdInt);
    }

    

}
