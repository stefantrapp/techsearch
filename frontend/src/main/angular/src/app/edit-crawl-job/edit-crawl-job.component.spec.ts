import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCrawlJobComponent } from './edit-crawl-job.component';

describe('EditCrawlJobComponent', () => {
  let component: EditCrawlJobComponent;
  let fixture: ComponentFixture<EditCrawlJobComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditCrawlJobComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditCrawlJobComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
