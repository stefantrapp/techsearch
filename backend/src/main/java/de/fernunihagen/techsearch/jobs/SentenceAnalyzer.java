package de.fernunihagen.techsearch.jobs;

import java.util.ArrayList;
import java.util.List;

import org.languagetool.tagging.de.GermanTagger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.nlp.ling.AbstractToken;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.semgraph.SemanticGraph;
/**
 * Klasse für die grammatikalische Analyse eines Satzes.
 */
public class SentenceAnalyzer {

    /**
     * Die Tags der Erkennung des part-of-speech (POS) stammen von hier: https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
     * 
     * Number  Tag     Description
     * 1.      CC      Coordinating conjunction
     * 2.      CD      Cardinal number
     * 3.      DT      Determiner
     * 4.      EX      Existential there
     * 5.      FW      Foreign word
     * 6.      IN      Preposition or subordinating conjunction
     * 7.      JJ      Adjective
     * 8.      JJR     Adjective, comparative
     * 9.      JJS     Adjective, superlative
     * 10.     LS      List item marker
     * 11.     MD      Modal
     * 12.     NN      Noun, singular or mass
     * 13.     NNS     Noun, plural
     * 14.     NNP     Proper noun, singular
     * 15.     NNPS    Proper noun, plural
     * 16.     PDT     Predeterminer
     * 17.     POS     Possessive ending
     * 18.     PRP     Personal pronoun
     * 19.     PRP$    Possessive pronoun
     * 20.     RB      Adverb
     * 21.     RBR     Adverb, comparative
     * 22.     RBS     Adverb, superlative
     * 23.     RP      Particle
     * 24.     SYM     Symbol
     * 25.     TO      to
     * 26.     UH      Interjection
     * 27.     VB      Verb, base form
     * 28.     VBD     Verb, past tense
     * 29.     VBG     Verb, gerund or present participle
     * 30.     VBN     Verb, past participle
     * 31.     VBP     Verb, non-3rd person singular present
     * 32.     VBZ     Verb, 3rd person singular present
     * 33.     WDT     Wh-determiner
     * 34.     WP      Wh-pronoun
     * 35.     WP$     Possessive wh-pronoun
     * 36.     WRB     Wh-adverb  
     * 
     *
     */
    
    public static String getModel(CoreSentence coreSentence, GermanTagger germanTagger) {
        // Visualisierung mit https://chaoticity.com/dependensee-a-dependency-parse-visualisation-tool/
        SemanticGraph graph = coreSentence.dependencyParse();
        
        var roots = graph.getRoots();
    
        SentModel model = new SentModel();
        
        if (!roots.isEmpty()) {
            var first = roots.iterator().next();
            
            if (isVerb(first)) {
                
                var verbLemm = getLemma(first, germanTagger);
                
                var subject = findSubject(graph, first, germanTagger);
                if (subject != null) {
                    model.addGrammar(subject, "S");
                    model.addGrammar(verbLemm, "P");
                }
            }

        }
        
        var vertexes = graph.vertexListSorted();
        for (var vertex : vertexes){
            String pos = vertex.get(PartOfSpeechAnnotation.class);
            
            vertex.backingLabel().isMWT();
            vertex.backingLabel().isMWTFirst();
            
            model.addPos(getLemma(vertex, germanTagger), pos);
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        String modelString;
        try {
            modelString = objectMapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        
        return modelString;
    }
    
    public static String getLemma(AbstractToken indexWord, GermanTagger germanTagger) {
        if (germanTagger != null) {
            var word = indexWord.word();
            var tags = germanTagger.tag(word);
            String lemma;
            if (tags.size() > 0) {
                lemma = tags.get(0).getLemma();
                if (lemma == null) {
                    lemma = word;
                }
            } else {
                lemma = word;
            }
            
            return lemma;
        } else {
            return indexWord.lemma();
        }
    }
    
    public static Iterable<SentModel> getSentModels(String[] sentences) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<SentModel> sentModels = new ArrayList<SentenceAnalyzer.SentModel>();
        try {
            for (var jsonSentModel : sentences) {
                var sentModel = objectMapper.readValue(jsonSentModel, SentModel.class);
                sentModels.add(sentModel);
            }
           return sentModels;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static boolean isVerb(IndexedWord word) {
        var pos = word.get(PartOfSpeechAnnotation.class);
        var isVerb = pos.startsWith("V");
        return isVerb;
    }
    
    private static String findSubject(SemanticGraph graph, IndexedWord word, GermanTagger germanTagger) {
        var subject = findWordWithRelationShortStart(graph, word, "nsubj");
        
        if (subject != null) {
            var compundWord = findWordWithRelationShortStart(graph, subject, "compound");
            
            if (compundWord != null) {
                return getLemma(compundWord, germanTagger) + " " + getLemma(subject, germanTagger);
            }
            
            return getLemma(subject, germanTagger);
        }
        
        return null;
    }
    
    private static IndexedWord findWordWithRelationShortStart(SemanticGraph graph, IndexedWord word, String prefix) {
        var edges = graph.getOutEdgesSorted(word);
        
        for (var edge : edges) {
            var edgeRel = edge.getRelation();
            var name = edgeRel.getShortName();
            if (name.startsWith(prefix)) { 
                return edge.getTarget();
            }
        }
            
        return null;
    }
    
    public static class SentModel {
        @JsonProperty("G")
        private List<List<String>> grammar;
        
        @JsonProperty("POS")
        private List<List<String>> pos;
        
        public void init() {
            
        }
        
        public void addGrammar(String value, String type) {
            if (getGrammar() == null) {
                grammar = new ArrayList<List<String>>();
            }
            
            AddToList(getGrammar(), value, type);
        }
        
        public void addPos(String value, String type) {
            if (getPos() == null) {
                pos = new ArrayList<List<String>>();
            }
            
            AddToList(getPos(), value, type);
        }
        
        private void AddToList(List<List<String>> list, String value, String type) {
            List<String> entry = new ArrayList<String>();
            list.add(entry);
            
            entry.add(value);
            entry.add(type);
        }

        public List<List<String>> getGrammar() {
            return grammar;
        }

        public List<List<String>> getPos() {
            return pos;
        }
    }
}
