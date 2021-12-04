import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApplicationJobName, JobStatusAllResponseDto, JobStatusReponseDto, RebuildSearchIndexJobDto, RebuildSearchIndexJobResponseDto, ResultDto, SettingDto, SettingsResponseDto, SettingUpdateDto, UpdateJobDto } from './dtos';
import { ServiceHelper } from './service-helper';
import { ServiceHelperFactory } from './service-helper-factory';

@Injectable({
  providedIn: 'root'
})
export class AdministrationService {

  private serviceHelper: ServiceHelper;

  constructor(serviceHelperFactory: ServiceHelperFactory) {
    this.serviceHelper = serviceHelperFactory.createServiceHelper("api/administration");
  }

  public getAllJobStates(): Observable<JobStatusAllResponseDto> {
    return this.serviceHelper.callService<JobStatusAllResponseDto>("get-job-status-all", null)
  }

  public getAllSettings(): Observable<SettingsResponseDto> {
    return this.serviceHelper.callService<SettingsResponseDto>("get-settings-all", null);
  }

  public saveSetting(setting: SettingUpdateDto): Observable<ResultDto> {
    return this.serviceHelper.callService<ResultDto>("update-setting", setting);
  }
  
  public cancelJob(jobName: ApplicationJobName) {
    const updateJobDto = new UpdateJobDto();
    updateJobDto.jobName = jobName;
    updateJobDto.cancel = true;
    
    return this.serviceHelper.callService<JobStatusReponseDto>("update-job", updateJobDto)
  }

  public scheduleJob(jobName: ApplicationJobName) {
    const updateJobDto = new UpdateJobDto();
    updateJobDto.jobName = jobName;
    updateJobDto.schedule = true;
    
    return this.serviceHelper.callService<JobStatusReponseDto>("update-job", updateJobDto)
  }
}