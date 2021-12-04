package de.fernunihagen.techsearch.restservice.dtos;

import java.util.Date;

import de.fernunihagen.techsearch.data.ApplicationJobEnum;

public class JobStatusReponseDto {

    private ApplicationJobEnum jobType;

    private boolean running;
    
    private Date startTime;
    
    private Date lastSignOfLife;
    
    // Für den Client in der Response
    private boolean success;
    
    public ApplicationJobEnum getJobType() {
        return jobType;
    }

    public void setJobType(ApplicationJobEnum jobType) {
        this.jobType = jobType;
    }

    private boolean shouldRun;
    
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

    public Date getLastSignOfLife() {
        return lastSignOfLife;
    }

    public void setLastSignOfLife(Date lastSignOfLife) {
        this.lastSignOfLife = lastSignOfLife;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    
}
