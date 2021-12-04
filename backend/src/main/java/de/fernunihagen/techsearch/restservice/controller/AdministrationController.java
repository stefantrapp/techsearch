package de.fernunihagen.techsearch.restservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.fernunihagen.techsearch.restservice.ApiResponse;
import de.fernunihagen.techsearch.restservice.dtos.JobStatusAllResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.JobStatusReponseDto;
import de.fernunihagen.techsearch.restservice.dtos.ResultDto;
import de.fernunihagen.techsearch.restservice.dtos.SettingUpdateDto;
import de.fernunihagen.techsearch.restservice.dtos.SettingsResponseDto;
import de.fernunihagen.techsearch.restservice.dtos.UpdateJobDto;
import de.fernunihagen.techsearch.restservice.services.AdministrationService;

@RestController
public class AdministrationController {

    @Autowired
    private AdministrationService administrationService;
    
    @PostMapping("/api/administration/get-job-status-all")
    public ApiResponse<JobStatusAllResponseDto> getJobStatusAll() {
        var response = administrationService.getJobStatusAll();
        
        return new ApiResponse<JobStatusAllResponseDto>(response);
    }
    
    @PostMapping("/api/administration/update-job")
    public ApiResponse<JobStatusReponseDto> updateJob(@RequestBody UpdateJobDto updateJobDto) {
        var response = new JobStatusReponseDto();
        
        administrationService.updateJob(updateJobDto);
        response.setSuccess(true);
        
        return new ApiResponse<JobStatusReponseDto>(response);
    }
    
    @PostMapping("/api/administration/get-settings-all")
    public ApiResponse<SettingsResponseDto> getSettingsAll() {
        var response = administrationService.getSettingsAll();
    
        return new ApiResponse<SettingsResponseDto>(response);
    }
    
    @PostMapping("/api/administration/update-setting")
    public ApiResponse<ResultDto> updateSetting(@RequestBody SettingUpdateDto settingUpdateDto) {
        administrationService.updateSetting(settingUpdateDto);
    
        return new ApiResponse<ResultDto>(new ResultDto());
    }
}
