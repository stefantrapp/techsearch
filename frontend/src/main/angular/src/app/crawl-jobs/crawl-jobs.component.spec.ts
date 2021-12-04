import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrawlJobsComponent } from './crawl-jobs.component';

describe('CrawlJobsComponent', () => {
  let component: CrawlJobsComponent;
  let fixture: ComponentFixture<CrawlJobsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CrawlJobsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CrawlJobsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
