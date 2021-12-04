package de.fernunihagen.techsearch.restservice.services;

import de.fernunihagen.techsearch.restservice.dtos.JobStatusAllResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.JobStatusRequestDto;
import de.fernunihagen.techsearch.restservice.dtos.JobStatusResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.SettingUpdateDto;
import de.fernunihagen.techsearch.restservice.dtos.SettingsResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.UpdateJobDto;

public interface AdministrationService {

    void schedulePreprocessDocumentsJob();
    void resetPreprocessDocumentsJob();
    void cancelPreprocessDocuments();

    void scheduleRebuildSent2VecModelJob();
    void resetSent2VecModelJob();
    
    void scheduleRebuildWord2VecModelJob();
    void resetWord2VecModelJob();
    
    void scheduleRebuildDoc2VecModelJob();
    void resetDoc2VecModelJob();
    
    void scheduleRebuildSearchIndexJob();
    void resetRebuildSearchIndexJob();
    
    void scheduleProcessCrawlJobsJob();
    void resetProcessCrawlJobsJob();
    
    boolean updateJob(UpdateJobDto updateProcessDto);
    
    JobStatusResponseDto getJobStatus(JobStatusRequestDto jobStatusRequestDto);
    
    JobStatusAllResponseDto getJobStatusAll();
    
    SettingsResponseDto getSettingsAll();
    
    void updateSetting(SettingUpdateDto settingUpdateDto);
}
