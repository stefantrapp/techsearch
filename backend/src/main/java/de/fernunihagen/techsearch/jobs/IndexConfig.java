package de.fernunihagen.techsearch.jobs;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import de.fernunihagen.techsearch.data.Language;

/**
 * Konfiguration des Lucene-Indexes. Die Werte werden aus der application.yaml gelesen.
 */
@Configuration
@ConfigurationProperties(prefix="index")
public class IndexConfig {

    private String storagePath;

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
    
    public String getLocalizedStoragePath(Language language) {
        var modelPath = Path.of(getStoragePath()).toAbsolutePath().normalize().toString();
        modelPath = modelPath.replace("{@Lang}", language.toString());
        return modelPath;
    }
    
}
