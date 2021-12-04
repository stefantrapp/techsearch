package de.fernunihagen.techsearch.restservice.dtos;

import java.util.Date;

import de.fernunihagen.techsearch.data.BulkImportJobType;

public class BulkImportJobDto {
    private int id;
    
    private String name;
    
    private String description;
    
    /**
     * Der Typ des Imports
     */
    private BulkImportJobType bulkImportJobType;

    /* Die Daten für den Massenimport. Die Daten sind abhängig vom Typ. Für EPO sind das Dateinamen
     *  
     */
    private String bulkImportData;
    
    private boolean finished;

    private Date startTime;
    
    private Date finishedTime;
    
    /* Wird auf dem Client gesetzt, wenn ein Crawljob gelöscht wird */
    private boolean deleted;
    
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
