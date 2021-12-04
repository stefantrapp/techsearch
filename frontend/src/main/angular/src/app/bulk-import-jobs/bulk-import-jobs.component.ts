import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { EditBulkImportJobComponent } from '../edit-bulk-import-job/edit-bulk-import-job.component';
import { MessageBoxResult } from '../message-box/message-box.component';
import { ApiOkResponseValue } from '../services/api-response';
import { BulkImportJobService } from '../services/bulk-import-job.service';
import { BulkImportJobDto, PageableDto } from '../services/dtos';
import { MessageBoxService } from '../services/message-box.service';
import { NotificationService } from '../services/notification.service';
import { ComponentBase } from '../shared/component-base';

type NewType = MatDialog;

@Component({
  selector: 'app-bulk-import-jobs',
  templateUrl: './bulk-import-jobs.component.html',
  styleUrls: ['./bulk-import-jobs.component.scss']
})
export class BulkImportJobsComponent extends ComponentBase implements OnInit {

  public jobs: BulkImportJobDto[] | undefined;

  public searchName: String | undefined = ""

  public pageable: PageableDto = new PageableDto();

  constructor(private dialog: MatDialog,
    private bulkImportJobService: BulkImportJobService,
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

  delete(job: BulkImportJobDto) {
    this.messageBoxService.queryYesNo(`Soll der Job ${job.name} gelöscht werden?`).subscribe(r => {
      if (r === MessageBoxResult.Yes) {
        this.bulkImportJobService.deleteBulkImportJob(job).subscribe(r => {
          if (r === ApiOkResponseValue) {
            this.notificationService.success(`Job ${job.name} wurde gelöscht.`);
            job.deleted = true;
          }
        });
      }
    });
  }

  editJob(job: BulkImportJobDto) {
    this.openDialog(job, true);
  }

  createJob() {
    const job = new BulkImportJobDto();
    this.openDialog(job, false);
  }

  openDialog(job: BulkImportJobDto, edit: boolean) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    
    dialogConfig.data = {
      "job": job,
      "edit": edit
    };

    const dialogRef = this.dialog.open(EditBulkImportJobComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      if (result === "saved") {
        if (edit) {
          this.bulkImportJobService.updateBulkImportJob(job).subscribe(result => {
            this.notificationService.success(`Job ${job.name} geändert.`);
          }, error => super.showError(error));
        } else  {
          this.bulkImportJobService.createBulkImportJob(job).subscribe(result => {
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
    this.bulkImportJobService.findBulkImportJobsByName("%" + this.searchName + "%", this.pageable).subscribe(r => {
      this.jobs = r.bulkImportJobs;
      
      if (r.pageable) {
        this.pageable.totalElements = r.pageable.totalElements;
      }
    })
  }

}
