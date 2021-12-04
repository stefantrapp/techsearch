import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditBulkImportJobComponent } from './edit-bulk-import-job.component';

describe('EditBulkImportJobComponent', () => {
  let component: EditBulkImportJobComponent;
  let fixture: ComponentFixture<EditBulkImportJobComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditBulkImportJobComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditBulkImportJobComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
