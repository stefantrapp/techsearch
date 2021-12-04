package de.fernunihagen.techsearch.restservice.dtos;

import de.fernunihagen.techsearch.data.Language;

public class TsneRequestDto {

    private String[] keys;
    
    private Language language;

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
    
}
