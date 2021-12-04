package de.fernunihagen.techsearch.data;

/**
 * Enum für das Format eines Dokuments.
 */
public enum DocumentFormat {

    XML, PLAINTEXT, HTML, MSWORD, UNKNOWN;
    
    public static DocumentFormat fromSuffix(String suffix) {
        
        if (suffix != null) {
            var lower = suffix.toLowerCase();
            
            if (lower.equals(".txt")) {
                return PLAINTEXT;
            } else if (lower.equals(".htm") || lower.equals(".html")) {
                return HTML;
            } else if (lower.equals(".docx")) {
                return MSWORD;
            } else if (lower.equals(".xml")) {
                return XML;
            }
        }
        
        return UNKNOWN;
    }
}
