import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CrawlJobDto } from '../services/dtos';

@Component({
  selector: 'app-edit-crawl-job',
  templateUrl: './edit-crawl-job.component.html',
  styleUrls: ['./edit-crawl-job.component.scss']
})
export class EditCrawlJobComponent implements OnInit {

  name = new FormControl('', [Validators.required]);
  description = new FormControl('', [Validators.required]);
  urls = new FormControl('', [Validators.required]);

  title = ""

  job: CrawlJobDto;
  edit: boolean;

  constructor(private dialogRef: MatDialogRef<EditCrawlJobComponent>,
    @Inject(MAT_DIALOG_DATA) data: any) { 
      this.job = data.job;
      this.edit = data.edit;
      if (data.edit) {
        this.title = "bearbeiten"
      } else {
        this.title = "erstellen"
      }
  }

  ngOnInit(): void {
    this.name.setValue(this.job?.name);
    this.urls.setValue(this.job?.urls);
    this.description.setValue(this.job?.description);
  }

  public save() {
    this.job.name = this.name.value;
    this.job.urls = this.urls.value;
    this.job.description = this.description.value;
    this.dialogRef.close("saved");
  }

  public cancel() {
    this.dialogRef.close("closed");
  }

}
