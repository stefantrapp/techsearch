import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CrawlJobDto, CrawlJobQueryDto, CrawlJobQueryResponseDto, PageableDto } from './dtos';
import { ServiceHelper } from './service-helper';
import { ServiceHelperFactory } from './service-helper-factory';

@Injectable({
  providedIn: 'root'
})
export class CrawlJobService {
  
  private serviceHelper: ServiceHelper;
  
  constructor(serviceHelperFactory: ServiceHelperFactory) {
    this.serviceHelper = serviceHelperFactory.createServiceHelper("api/crawljob");
  }

  public findCrawlJobsByName(name: string, pageable: PageableDto): Observable<CrawlJobQueryResponseDto> {
    const crawlJobQueryDto =  new CrawlJobQueryDto(); 
    crawlJobQueryDto.name = name;
    crawlJobQueryDto.pageable = pageable;

    return this.serviceHelper.callService<CrawlJobQueryResponseDto>("search", crawlJobQueryDto);
  }

  public deleteCrawlJob(crawlJobDto: CrawlJobDto): Observable<String> {
    return this.serviceHelper.callService<String>("delete", crawlJobDto);
  }

  public createCrawlJob(crawlJobDto: CrawlJobDto): Observable<String> {
    return this.serviceHelper.callService<String>("create", crawlJobDto);
  }

  public updateCrawlJob(crawlJobDto: CrawlJobDto): Observable<String> {
    return this.serviceHelper.callService<String>("update", crawlJobDto);
  }
 }
