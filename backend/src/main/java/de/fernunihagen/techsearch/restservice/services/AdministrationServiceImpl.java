package de.fernunihagen.techsearch.restservice.services;

import java.util.ArrayList;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fernunihagen.techsearch.data.ApplicationJobEnum;
import de.fernunihagen.techsearch.repository.ApplicationJobStateRepository;
import de.fernunihagen.techsearch.repository.SettingRepository;
import de.fernunihagen.techsearch.restservice.dtos.JobStatusAllResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.JobStatusRequestDto;
import de.fernunihagen.techsearch.restservice.dtos.JobStatusResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.SettingDto;
import de.fernunihagen.techsearch.restservice.dtos.SettingUpdateDto;
import de.fernunihagen.techsearch.restservice.dtos.SettingsResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.UpdateJobDto;

@Service()
public class AdministrationServiceImpl implements AdministrationService {

    @Autowired
    private ApplicationJobStateRepository applicationJobStateRepository;
    
    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    public void schedulePreprocessDocumentsJob() {
        scheduleJob(ApplicationJobEnum.PREPROCESS_DOCUMENTS);
    }
    
    @Override
    public void scheduleRebuildSent2VecModelJob() {
        scheduleJob(ApplicationJobEnum.REBUILD_SENT2VEC_MODEL);
    }
    
    @Override
    public void cancelPreprocessDocuments() {
        var jobState = applicationJobStateRepository.findApplicationJobState(ApplicationJobEnum.PREPROCESS_DOCUMENTS);
        jobState.setShouldRun(false);
        applicationJobStateRepository.save(jobState);
    }

	@Override
	public void resetSent2VecModelJob() {
	    resetJob(ApplicationJobEnum.REBUILD_SENT2VEC_MODEL);
	}

    @Override
    public void scheduleRebuildWord2VecModelJob() {
        scheduleJob(ApplicationJobEnum.REBUILD_WORD2VEC_MODEL);
    }
    
    @Override
    public void resetWord2VecModelJob() {
        resetJob(ApplicationJobEnum.REBUILD_WORD2VEC_MODEL);
    }

    @Override
    public void resetPreprocessDocumentsJob() {
        resetJob(ApplicationJobEnum.PREPROCESS_DOCUMENTS);
    }

    @Override
    public void scheduleRebuildSearchIndexJob() {
        scheduleJob(ApplicationJobEnum.REBUILD_SEARCH_INDEX);
    }

    @Override
    public void resetRebuildSearchIndexJob() {
        resetJob(ApplicationJobEnum.REBUILD_SEARCH_INDEX);
    }
    
    @Override
    public void scheduleProcessCrawlJobsJob() {
        scheduleJob(ApplicationJobEnum.PROCESS_CRAWL_JOBS);
        
    }

    @Override
    public void scheduleRebuildDoc2VecModelJob() {
        scheduleJob(ApplicationJobEnum.REBUILD_DOC2VEC_MODEL);
        
    }

    @Override
    public void resetDoc2VecModelJob() {
        resetJob(ApplicationJobEnum.REBUILD_DOC2VEC_MODEL);
    }
    
    @Override
    public void resetProcessCrawlJobsJob() {
        resetJob(ApplicationJobEnum.PROCESS_CRAWL_JOBS);
        
    }
    
    private void scheduleJob(ApplicationJobEnum applicationJobEnum) {
        var jobState = applicationJobStateRepository.findApplicationJobState(applicationJobEnum);
        jobState.setShouldRun(true);
        applicationJobStateRepository.save(jobState);
    }
    
    
    public void cancelJob(ApplicationJobEnum applicationJobEnum) {
        var jobState = applicationJobStateRepository.findApplicationJobState(applicationJobEnum);
        jobState.setShouldRun(false);
        applicationJobStateRepository.save(jobState);
    }
    
    private void resetJob(ApplicationJobEnum applicationJobEnum) {
        var jobState = applicationJobStateRepository.findApplicationJobState(applicationJobEnum);
        jobState.setShouldRun(false);
        jobState.setRunning(false);
        applicationJobStateRepository.save(jobState);
    }

    @Override
    public boolean updateJob(UpdateJobDto updateJobDto) {
        var jobName = updateJobDto.getJobName().toString();
        
        var job = ApplicationJobEnum.fromName(jobName);
        if (updateJobDto.isSchedule()) {
            scheduleJob(job);
        } else if (updateJobDto.isReset()) {
            resetJob(job);
        } else if (updateJobDto.isCancel()) {
            cancelJob(job);
        }
                
        return true;
    }

    @Override
    public JobStatusResponseDto getJobStatus(JobStatusRequestDto jobStatusRequestDto) {
        var job = ApplicationJobEnum.fromName(jobStatusRequestDto.getJobName().toString());
        var jobState = applicationJobStateRepository.findApplicationJobState(job);
        
        var jobStatusResponseDto = modelMapper.map(jobState, JobStatusResponseDto.class);
        
        return jobStatusResponseDto;
    }

    @Override
    public JobStatusAllResponseDto getJobStatusAll() {
        var response = new JobStatusAllResponseDto();
        var stateList = new ArrayList<JobStatusResponseDto>();
        response.setJobStatus(stateList);
        
        var allJobState = applicationJobStateRepository.findAll();
        for (var jobState : allJobState) {
            var jobStatusResponseDto = modelMapper.map(jobState, JobStatusResponseDto.class);
            stateList.add(jobStatusResponseDto);
        }
        
        return response;
    }

    @Override
    public SettingsResponseDto getSettingsAll() {
        var allSettings = settingRepository.findAll();
        
        var result = new SettingsResponseDto();
        var settings = new ArrayList<SettingDto>();
        result.setSettings(settings);
        
        allSettings.forEach(s -> {
            settings.add(modelMapper.map(s, SettingDto.class));    
        });
        
        return result;
    }

    @Override
    public void updateSetting(SettingUpdateDto settingUpdateDto) {
        var setting = settingRepository.findByKey(settingUpdateDto.getKey());
        setting.setValue(settingUpdateDto.getValue());
        settingRepository.save(setting);
    }
}
