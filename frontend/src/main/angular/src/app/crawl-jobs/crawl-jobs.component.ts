import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { CrawlJobService } from '../services/crawl-job.service';
import { EditCrawlJobComponent } from '../edit-crawl-job/edit-crawl-job.component';
import { CrawlJobDto, PageableDto } from '../services/dtos';
import { NotificationService } from '../services/notification.service';
import { MessageBoxService } from '../services/message-box.service';
import { MessageBoxResult } from '../message-box/message-box.component';
import { PageEvent } from '@angular/material/paginator';
import { ApiOkResponseValue } from '../services/api-response';
import { ComponentBase } from '../shared/component-base';

@Component({
  selector: 'app-crawl-jobs',
  templateUrl: './crawl-jobs.component.html',
  styleUrls: ['./crawl-jobs.component.scss']
})
export class CrawlJobsComponent extends ComponentBase implements OnInit {

  public jobs: CrawlJobDto[] | undefined;

  public searchName: String | undefined = ""

  public pageable: PageableDto = new PageableDto();

  constructor(private dialog: MatDialog,
    private crawlJobService: CrawlJobService,
    protected notificationService: NotificationService,
    private messageBoxService: MessageBoxService) {
    super(notificationService);
  }

  ngOnInit(): void {
    this.pageable = new PageableDto();
    this.pageable.page = 0;
    this.pageable.size = 10
    this.pageable.sortDir = "DESC";
    this.pageable.sort = "id";
  }

  delete(job: CrawlJobDto) {
    this.messageBoxService.queryYesNo(`Soll der Job ${job.name} gelöscht werden?`).subscribe(r => {
      if (r === MessageBoxResult.Yes) {
        this.crawlJobService.deleteCrawlJob(job).subscribe(r => {
          if (r === ApiOkResponseValue) {
            this.notificationService.success(`Job ${job.name} wurde gelöscht.`);
            job.deleted = true;
          }
        });
      }
    });
  }

  editJob(job: CrawlJobDto) {
    this.openDialog(job, true);
  }

  createJob() {
    const job = new CrawlJobDto();
    this.openDialog(job, false);
  }

  openDialog(job: CrawlJobDto, edit: boolean) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    
    dialogConfig.data = {
      "job": job,
      "edit": edit
    };

    const dialogRef = this.dialog.open(EditCrawlJobComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      if (result === "saved") {
        if (edit) {
          this.crawlJobService.updateCrawlJob(job).subscribe(result => {
            this.notificationService.success(`Job ${job.name} geändert.`);
          }, error => super.showError(error));
        } else  {
          this.crawlJobService.createCrawlJob(job).subscribe(result => {
            this.notificationService.success(`Job ${job.name} angelegt.`);
          }, error => super.showError(error));
        }
      }

    });
  }

  updatePaging(pageEvent: PageEvent) {
    this.pageable.page = pageEvent.pageIndex;
    this.search();
  }

  search() {
    this.crawlJobService.findCrawlJobsByName("%" + this.searchName + "%", this.pageable).subscribe(r => {
      this.jobs = r.crawlJobs;
      
      if (r.pageable) {
        this.pageable.totalElements = r.pageable.totalElements;
      }
    })
  }

}

