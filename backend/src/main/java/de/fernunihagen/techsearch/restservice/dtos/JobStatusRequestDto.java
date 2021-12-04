package de.fernunihagen.techsearch.restservice.dtos;

import de.fernunihagen.techsearch.data.ApplicationJobName;

public class JobStatusRequestDto {

    private ApplicationJobName jobName;

    public ApplicationJobName getJobName() {
        return jobName;
    }

    public void setJobName(ApplicationJobName jobName) {
        this.jobName = jobName;
    }
    
}
