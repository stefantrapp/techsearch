package de.fernunihagen.techsearch.restservice.dtos;

import de.fernunihagen.techsearch.data.ApplicationJobName;

public class UpdateJobDto {

    private ApplicationJobName jobName;
    
    private boolean schedule;
    
    private boolean reset;
    
    private boolean cancel;

    public boolean isSchedule() {
        return schedule;
    }

    public void setSchedule(boolean schedule) {
        this.schedule = schedule;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public ApplicationJobName getJobName() {
        return jobName;
    }

    public void setJobName(ApplicationJobName jobName) {
        this.jobName = jobName;
    }

    
    
}
