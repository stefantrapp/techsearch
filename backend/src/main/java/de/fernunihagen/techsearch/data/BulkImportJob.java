package de.fernunihagen.techsearch.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Job für den Massenimport von Daten.
 */

@Entity
public class BulkImportJob {
 
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
     * Der Typ des Imports
     */
    private BulkImportJobType bulkImportJobType;

    /* Die Daten für den Massenimport. Die Daten sind abhängig vom Typ. Für EPO sind das Dateinamen auf die der Server zugreifen kann.
     *  
     */
    private String bulkImportData;
    
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
    
    public BulkImportJobType getBulkImportJobType() {
        return bulkImportJobType;
    }

    public void setBulkImportJobType(BulkImportJobType bulkImportJobType) {
        this.bulkImportJobType = bulkImportJobType;
    }

    public String getBulkImportData() {
        return bulkImportData;
    }

    public void setBulkImportData(String bulkImportData) {
        this.bulkImportData = bulkImportData;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
