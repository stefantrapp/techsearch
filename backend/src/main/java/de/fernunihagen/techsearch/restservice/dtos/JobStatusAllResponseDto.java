package de.fernunihagen.techsearch.restservice.dtos;

import java.util.List;

public class JobStatusAllResponseDto {

    private List<JobStatusResponseDto>  jobStatus;

    public List<JobStatusResponseDto> getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(List<JobStatusResponseDto> jobStatus) {
        this.jobStatus = jobStatus;
    }
}
