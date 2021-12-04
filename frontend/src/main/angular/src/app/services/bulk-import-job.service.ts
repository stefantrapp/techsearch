import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BulkImportJobDto, BulkImportJobQueryDto, BulkImportJobQueryResponseDto, PageableDto } from './dtos';
import { ServiceHelper } from './service-helper';
import { ServiceHelperFactory } from './service-helper-factory';

@Injectable({
  providedIn: 'root'
})
export class BulkImportJobService {

  private serviceHelper: ServiceHelper

  constructor(serviceHelperFactory: ServiceHelperFactory) {
    this.serviceHelper = serviceHelperFactory.createServiceHelper("api/bulkimport");
  }

  public findBulkImportJobsByName(name: string, pageable: PageableDto): Observable<BulkImportJobQueryResponseDto> {
    const bulkImportJobQueryDto =  new BulkImportJobQueryDto(); 
    bulkImportJobQueryDto.name = name;
    bulkImportJobQueryDto.pageable = pageable;

    return this.serviceHelper.callService<BulkImportJobQueryResponseDto>("search", bulkImportJobQueryDto);
  }

  public deleteBulkImportJob(bulkImportJobDto: BulkImportJobDto): Observable<String> {
    return this.serviceHelper.callService<String>("delete", bulkImportJobDto);
  }

  public createBulkImportJob(crawlJobDto: BulkImportJobDto): Observable<String> {
    return this.serviceHelper.callService<String>("create", crawlJobDto);
  }

  public updateBulkImportJob(crawlJobDto: BulkImportJobDto): Observable<String> {
    return this.serviceHelper.callService<String>("update", crawlJobDto);
  }
}
