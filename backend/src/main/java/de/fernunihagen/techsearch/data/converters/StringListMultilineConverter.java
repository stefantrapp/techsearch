package de.fernunihagen.techsearch.data.converters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Konverter zur Speicherung einer Liste von String als eine String mit mehreren Zeilen (die einzelnen String in der Liste) in der Datenbank. 
 */

@Converter
public class StringListMultilineConverter implements AttributeConverter<List<String>, String> {
    
    private static final String SPLIT_CHAR = "\n";
    
    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        String result;
        
        if (stringList != null) {
            result = String.join(SPLIT_CHAR, stringList);
        } else {
            result =  "";
        }
        
        return result;
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        List<String> result;
        
        if (string != null) {
            result = Arrays.asList(string.split(SPLIT_CHAR));
        } else {
            result =  Collections.emptyList();
        }
        
        return result;
    }
}