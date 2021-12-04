package de.fernunihagen.techsearch.data;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import de.fernunihagen.techsearch.data.converters.StringListMultilineConverter;

/**
 * Job für einen Web-Crawl-Job
 */

@Entity
public class CrawlJob {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private int id;

    /**
     * Logischer Name des Jobs.
     */
    private String name;
    
    /**
     * Beschreibung des Jobs.
     */
    private String description;
    
    /**
     * Git an, ob der Job beendet ist, oder nicht.
     */
    private boolean finished;

    /**
     * Startzeit des Jobs. 
     */
    private Date startTime;
    
    /**
     * Zeitpunt, an dem der Job beendet wurde.
     */
    private Date finishedTime;
    
    /**
     * List mit URLs mit denen das Webcrawling begonnen werden soll (sog. Seeds). 
     */
    @Convert(converter = StringListMultilineConverter.class)
    private List<String> urls;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(Date finishedTime) {
        this.finishedTime = finishedTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
