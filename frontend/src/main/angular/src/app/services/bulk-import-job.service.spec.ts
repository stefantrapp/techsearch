import { TestBed } from '@angular/core/testing';

import { BulkImportJobService } from './bulk-import-job.service';

describe('BulkImportJobService', () => {
  let service: BulkImportJobService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BulkImportJobService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
