import { TestBed } from '@angular/core/testing';

import { CrawlJobService } from './crawl-job.service';

describe('CrawlJobService', () => {
  let service: CrawlJobService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CrawlJobService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
