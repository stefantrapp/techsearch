package de.fernunihagen.techsearch.jobs.crawling;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguration für das Web-Crawling. Die Werte werden aus der application.yaml gelesen. 
 */

@Configuration
@ConfigurationProperties(prefix="crawling")
public class CrawlingConfig {

    private String tempStoragePath;

    private int cleanupDelaySeconds;
    
    private int threadShutdownDelaySeconds;
    
    private int threadMonitoringDelaySeconds;
    
    private int politenessDelay;
    
    private int numberOfCrawlers;
    
    public String getTempStoragePath() {
        return tempStoragePath;
    }

    public void setTempStoragePath(String tempStoragePath) {
        this.tempStoragePath = tempStoragePath;
    }

    public int getCleanupDelaySeconds() {
        return cleanupDelaySeconds;
    }

    public void setCleanupDelaySeconds(int cleanupDelaySeconds) {
        this.cleanupDelaySeconds = cleanupDelaySeconds;
    }

    public int getThreadShutdownDelaySeconds() {
        return threadShutdownDelaySeconds;
    }

    public void setThreadShutdownDelaySeconds(int threadShutdownDelaySeconds) {
        this.threadShutdownDelaySeconds = threadShutdownDelaySeconds;
    }

    public int getThreadMonitoringDelaySeconds() {
        return threadMonitoringDelaySeconds;
    }

    public void setThreadMonitoringDelaySeconds(int threadMonitoringDelaySeconds) {
        this.threadMonitoringDelaySeconds = threadMonitoringDelaySeconds;
    }

    public int getPolitenessDelay() {
        return politenessDelay;
    }

    public void setPolitenessDelay(int politenessDelay) {
        this.politenessDelay = politenessDelay;
    }

    public int getNumberOfCrawlers() {
        return numberOfCrawlers;
    }

    public void setNumberOfCrawlers(int numberOfCrawlers) {
        this.numberOfCrawlers = numberOfCrawlers;
    }
}
