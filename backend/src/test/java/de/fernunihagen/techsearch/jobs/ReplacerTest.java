package de.fernunihagen.techsearch.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class ReplacerTest {
    
    @Test
    @Ignore
    public void testReplacer() {
                
        var text = "D0a1s2 3i4s5t6 7e8i9n0 T(e)s[t]. Das {ist} noch ein Test. 312.123.867,45 Euro. 312,123,867.45 Dollar.";
        
        var result = PreprocessDocumentJob.replaceNonCharacters(text);
        
        assertEquals("Das ist ein Test. Das ist noch ein Test.  Euro.  Dollar.", result);
    }
    
}
