package de.fernunihagen.techsearch.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import de.fernunihagen.techsearch.data.converters.ApplicationJobConverter;

/*
 * Klasse für die Jobs der Anwendung
 */

@Entity
public class ApplicationJobState {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private int id;
    
    @Convert(converter = ApplicationJobConverter.class)
    private ApplicationJobEnum jobType;

    /**
     * Gibt an, dass der Job laufen soll, oder nicht. Die Jobs prüften periodische, ob dieser Wert auf "true" gesetzt ist
     * und beenden sich dann. Es ist kein harter Abbruch und das Abbrechen kann einige Zeit (ja nach Job einige Minuten dauern) 
     */
    
    private boolean shouldRun;
    
    /**
     * Gibt an, ob der Job gerade läuft, oder nicht.
     */
    
    private boolean running;
    
    /**
     * Zeitpunkt des Start des Jobs
     */
    
    private Date startTime;
    
    /**
     * Zeitpunkt, an dem der Job zuletzt während eines Lauf aktiv war. wenn der Job läuft (running = true) und 
     * lastSignOfLife länger nicht mehr aktualisiert wurde, kann das daruf hindeuten, dass der Job abgebrochen ist. 
     */
    private Date lastSignOfLife;
    
    public ApplicationJobEnum getJobType() {
        return jobType;
    }

    public void setJobType(ApplicationJobEnum jobType) {
        this.jobType = jobType;
    }

    public boolean isShouldRun() {
        return shouldRun;
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Date getLastSignOfLife() {
        return lastSignOfLife;
    }

    public void setLastSignOfLife(Date lastSignOfLife) {
        this.lastSignOfLife = lastSignOfLife;
    } 
    
}
