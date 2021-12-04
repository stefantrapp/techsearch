package de.fernunihagen.techsearch.data;

/**
 * Enum für eine unterstützte Sprache. Franzörisch wird nicht unterstützt, kommt aber in den EPO-Dateien vor.
 */

public enum Language {

    ENGLISH("en"), 
    GERMAN("de"), 
    UNKNOWN("unknown"),
    /**
     * Französisch wird zwar von der Anwendung nicht unterstüzt, aber die Patente von EPO enthalten auch Dokumente nur in französischer Sprache.
     * Dieser Enum, dient dazu, diese Information zu erhalten. 
     */
    FRENCH("fr");
    
    private String name;

    private Language(String name) {
        this.name = name;
        
    }
    
    public static Language fromString(String language) {
        
        if (language != null) {
            var lower = language.toLowerCase();
            
            if (lower.equals(ENGLISH.name)) {
                return ENGLISH;
            } else if (lower.equals(GERMAN.name)) {
                return GERMAN;
            } else if (lower.equals(FRENCH.name)) {
                return FRENCH;
            }
        }
        
        return UNKNOWN;
    }
    
    
    public static String toString(Language language) {
        return language.name;
    } 
    
}
