package de.fernunihagen.techsearch.restservice.services;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="sent2vec")
public class Sent2VecConfig {

    private String executablePath;

    private String modelPath;
    
    private String sentencesPath;

	public String getExecutablePath() {
		return executablePath;
	}

	public void setExecutablePath(String executablePath) {
		this.executablePath = executablePath;
	}

	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	public String getSentencesPath() {
		return sentencesPath;
	}

	public void setSentencesPath(String sentencesPath) {
		this.sentencesPath = sentencesPath;
	}
    
    
    
}
