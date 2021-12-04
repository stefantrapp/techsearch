package de.fernunihagen.techsearch.restservice.dtos;

import java.util.Date;

import de.fernunihagen.techsearch.data.ApplicationJobName;

public class JobStatusResponseDto {

    private ApplicationJobName jobName;

    private Date lastSignOfLife;
    
    private boolean shouldRun;
    
    private boolean running;
    
    private Date startTime;
    
    public Date getLastSignOfLife() {
        return lastSignOfLife;
    }

    public void setLastSignOfLife(Date lastSignOfLife) {
        this.lastSignOfLife = lastSignOfLife;
    }

    public boolean isShouldRun() {
        return shouldRun;
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public ApplicationJobName getJobName() {
        return jobName;
    }

    public void setJobName(ApplicationJobName jobName) {
        this.jobName = jobName;
    }
    
}
