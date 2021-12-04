import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BulkImportJobsComponent } from './bulk-import-jobs.component';

describe('BulkImportJobsComponent', () => {
  let component: BulkImportJobsComponent;
  let fixture: ComponentFixture<BulkImportJobsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BulkImportJobsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BulkImportJobsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
